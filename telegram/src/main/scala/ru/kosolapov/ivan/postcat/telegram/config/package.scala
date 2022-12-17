package ru.kosolapov.ivan.postcat.telegram

import cats.effect.Sync
import ciris._
import ciris.circe.yaml.circeYamlConfigDecoder
import com.comcast.ip4s.Port
import io.circe.{Decoder, HCursor}
import org.http4s.Uri
import org.http4s.circe._
import cats.syntax.all._
import ru.kosolapov.ivan.postcat.common.config.implicits._
import ru.kosolapov.ivan.postcat.common.config.YamlConfig

package object config {
  final case class BotToken(botToken: Secret[String])

  final case class AppConfig(coreUri: Uri)

  object AppConfig {
    implicit val appConfigDecoder: Decoder[AppConfig] = (c: HCursor) => for {
      uri <- c.downField("core").downField("url").as[Uri]
    } yield AppConfig(uri)

    implicit val decoder: ConfigDecoder[String, AppConfig] =
      circeYamlConfigDecoder("AppConfig")
  }

  /**
   * Getting bot telegram token from env
   */
  val botConfig: ConfigValue[Effect, BotToken] =
      env("BOT_TOKEN").secret.map(BotToken)

  /**
   * Getting app config with port and url of core service
   */
  def appConfig[F[_]: Sync]: ConfigValue[F, AppConfig] = YamlConfig.get.as[AppConfig]

}