package ru.kosolapov.ivan.postcat.common.config

import cats.MonadThrow
import cats.effect.{Resource, Sync}
import ciris._
import cats.syntax.all._

import java.io.{BufferedReader, InputStreamReader}
import java.util.stream.Collectors

object YamlConfig {

  private val CONFIG_RESOURCE = "/config/application.yaml"

  /**
   * Read yaml config from [[CONFIG_RESOURCE]]
   */
  def get[F[_] : Sync : MonadThrow]: ConfigValue[F, String] = ConfigValue.eval {
    val configLines = Resource.make {
      Sync[F].blocking(new BufferedReader(
        new InputStreamReader(getClass.getResourceAsStream(CONFIG_RESOURCE))
      ).lines()
      )
    } { stream => Sync[F].blocking(stream.close()) }

    configLines
      .use(s => Sync[F].blocking(s.collect(Collectors.joining("\n"))))
      .map(ConfigValue.default)
  }

}
