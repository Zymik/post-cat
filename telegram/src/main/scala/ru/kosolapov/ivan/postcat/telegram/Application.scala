package ru.kosolapov.ivan.postcat.telegram

import cats.effect._
import com.comcast.ip4s.IpLiteralSyntax
import org.http4s.client.Client
import org.http4s.client.middleware.Logger
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.typelevel.log4cats.slf4j.Slf4jLogger
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.telegram.bot.PostCatBot
import ru.kosolapov.ivan.postcat.telegram.client.ChatClientImpl
import ru.kosolapov.ivan.postcat.telegram.config._
import telegramium.bots._
import telegramium.bots.high.implicits.methodOps
import telegramium.bots.high.{Api, BotApi, Methods}

object Application extends IOApp {

  type CatsLogger[F[_]] = org.typelevel.log4cats.Logger[F]

  private val logger: Client[IO] => Client[IO] = Logger[IO](logHeaders = true, logBody = true)

  private def client(token: String, appConfig: AppConfig)(implicit catsLogger: CatsLogger[IO]) = {
    for {
      client <- EmberClientBuilder.default[IO].build.map(logger(_))
      bot <- getBot(token, appConfig, client)
    } yield bot
  }

  private def getBot(token: String, appConfig: AppConfig, client: Client[IO])(implicit catsLogger: CatsLogger[IO]) = {
    implicit val api: Api[IO] = BotApi(client, baseUrl = s"https://api.telegram.org/bot$token")
    for {
      botId <- Resource.eval(Methods.getMe().exec)
      bot <- startUpBot(botId, client, appConfig)
    } yield bot
  }

  private def startUpBot(botUser: User, client: Client[IO], appConfig: AppConfig)
                    (implicit catsLogger: CatsLogger[IO], api: Api[IO]): Resource[IO, Unit] = {
    val botId = TelegramUserId(botUser.id)
    val chatClient = new ChatClientImpl[IO](botId)

    val serviceConfiguration = new ServiceConfiguration[IO](
      client,
      chatClient,
      appConfig.coreUri
    )

    val controllerConfiguration = new ControllerConfiguration[IO](
      serviceConfiguration
    )

    val commandConfiguration = new BotCommandsConfiguration[IO](
      controllerConfiguration
    )

    val bot = new PostCatBot[IO](
      commandConfiguration.commands,
      chatClient
    )

    val server = EmberServerBuilder.default[IO]
      .withPort(port"8090")
      .withHost(ip"0.0.0.0")
      .withHttpApp(
        RoutesConfiguration.getRoutes(
          controllerConfiguration
        ).orNotFound)
      .build


    val publicCommands = Methods.setMyCommands(
      commandConfiguration
        .commands
        .description
        .map(d => BotCommand(d.name, d.description))
    ).exec

    server.evalMap(
      _ => publicCommands >> bot.start()
    )


  }

  implicit def logger[F[_] : Sync]: CatsLogger[F] = Slf4jLogger.getLogger[F]

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      config <- botConfig.load[IO]
      appConfig <- appConfig[IO].load
      _ <- client(config.botToken.value, appConfig)(logger[IO]).use(_ => IO.never)
    } yield ExitCode.Success
  }
}
