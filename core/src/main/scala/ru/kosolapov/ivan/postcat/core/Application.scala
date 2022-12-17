package ru.kosolapov.ivan.postcat.core

import cats.effect.{ExitCode, IO, IOApp, Sync}
import com.comcast.ip4s.IpLiteralSyntax
import doobie.hikari.HikariTransactor
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Method, Request}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import ru.kosolapov.ivan.postcat.common.config.database.migration.FlywayMigration
import ru.kosolapov.ivan.postcat.common.config.{DatabaseConfiguration, database}
import ru.kosolapov.ivan.postcat.core.client.telegram.TelegramClientImpl
import ru.kosolapov.ivan.postcat.core.config._


object Application extends IOApp {

  implicit def logger[F[_] : Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  def run(args: List[String]): IO[ExitCode] = {
    for {
      jdbcConfig <- database.jdbcConfig.load[IO]
      flywayConfig <- database.flywayConfig[IO].load
      _ <- FlywayMigration.migrate[IO](flywayConfig)
      _ <- (for {
        xa <- DatabaseConfiguration.getTransactor[IO](jdbcConfig)
        client <- EmberClientBuilder.default[IO].build
      } yield (xa, client)).use {
        case (xa, client) =>
          configureClient(xa, client)
      }
    }
    yield ExitCode.Success
  }

  private def configureClient(xa: HikariTransactor[IO], client: Client[IO]) = {
    val request = Request[IO](method = Method.POST, uri = uri"http://localhost:8090/channel/post")

    val repositoryConfiguration = new RepositoryConfiguration[IO](xa)

    val serviceConfiguration = new ServiceConfiguration[IO](
      repositoryConfiguration,
      new TelegramClientImpl(
        client,
        request
      )
    )

    val controllerConfiguration = new ControllerConfiguration[IO](
      serviceConfiguration
    )

    val securityEndpointConfiguration = new SecurityEndpointConfiguration[IO](
      serviceConfiguration
    )

    EmberServerBuilder.default[IO]
      .withPort(port"8888")
      .withHost(ip"0.0.0.0")
      .withHttpApp(
        RoutesConfiguration.getRoutes(
          controllerConfiguration,
          securityEndpointConfiguration
        ).orNotFound
      )
      .build
      .use(_ => IO.never)
  }
}
