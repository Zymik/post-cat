package ru.kosolapov.ivan.postcat.api

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.IpLiteralSyntax
import org.http4s.ember.server.EmberServerBuilder
import ru.kosolapov.ivan.postcat.common.config.DatabaseConfiguration
import ru.kosolapov.ivan.postcat.common.config.database.jdbcConfig
import ru.kosolapov.ivan.postcat.api.config.{ControllerConfiguration, RepositoryConfiguration, RoutesConfiguration, ServiceConfiguration}

object Application extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    for {
      jdbcConfig <- jdbcConfig.load[IO]
      _ <- DatabaseConfiguration.getTransactor[IO](jdbcConfig).use(
        transactor => {
          val repositoryConfiguration = new RepositoryConfiguration[IO](transactor)
          val serviceConfiguration = new ServiceConfiguration[IO](repositoryConfiguration)
          val controllerConfiguration = new ControllerConfiguration[IO](serviceConfiguration)
          val routes = RoutesConfiguration.getRoutes(
            controllerConfiguration,
            serviceConfiguration
          )
          EmberServerBuilder.default[IO]
            .withPort(port"9090")
            .withHost(ip"0.0.0.0")
            .withHttpApp(
              routes.orNotFound
            )
            .build
            .use(_ => IO.never)
        }
      )
    } yield ExitCode.Success
  }
}
