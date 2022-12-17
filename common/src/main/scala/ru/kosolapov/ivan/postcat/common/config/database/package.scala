package ru.kosolapov.ivan.postcat.common.config

import cats.effect.Sync
import cats.syntax.all._
import ciris._
import ciris.circe.yaml.circeYamlConfigDecoder
import io.circe.{Decoder, HCursor}

package object database {

  private case class FlywayData
  (
    migrationTable: String,
    migrationLocations: List[String]
  )

  private object FlywayData {
    implicit val decoder: Decoder[FlywayData] =
      (c: HCursor) => {
        val cursor = c.downField("flyway")
        for {
          table <- cursor.downField("migrationTable").as[String]
          list <- cursor.downField("migrationLocations").as[List[String]]
        } yield FlywayData(table, list)
      }

    implicit val configDecoder: ConfigDecoder[String, FlywayData] =
      circeYamlConfigDecoder("FlywayData")
  }


  val jdbcConfig: ConfigValue[Effect, JdbcConfig] =
    (
      env("POSTGRES_USER"),
      env("POSTGRES_PASSWORD").secret,
      env("POSTGRES_URL"),
      ).parMapN(JdbcConfig)

  def flywayConfig[F[_]: Sync]: ConfigValue[F, FlywayConfig] = {
    (
      YamlConfig.get.as[FlywayData],
      jdbcConfig
      ).parMapN {
      case (FlywayData(migrationTable, migrationLocations), jdbcConfig: JdbcConfig) =>
        FlywayConfig(jdbcConfig, migrationTable, migrationLocations)
    }
  }
}
