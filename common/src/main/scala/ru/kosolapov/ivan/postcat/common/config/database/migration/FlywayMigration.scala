package ru.kosolapov.ivan.postcat.common.config.database.migration

import cats.MonadThrow
import cats.effect.Sync
import cats.implicits._
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.syntax._
import ru.kosolapov.ivan.postcat.common.config.database.FlywayConfig

object FlywayMigration {
  /**
   * Run migrations specified by [[FlywayConfig]]
   */
  def migrate[F[_] : Sync : Logger : MonadThrow](config: FlywayConfig): F[Unit] = {
    for {
      _ <- info"Running migrations from locations: ${config.migrationLocations.mkString(",")}"
      fluentConfig <- getConfiguration(config)
      migrationsCount <- migrate(fluentConfig)
      _ <- info"Executed $migrationsCount migrations"
    } yield ()
  }

  private def getConfiguration[F[_] : Sync](config: FlywayConfig): F[FluentConfiguration] = {
    val jdbcConfig = config.jdbcConfig
    Sync[F].delay(
      Flyway.configure(classOf[org.postgresql.Driver].getClassLoader)
        .dataSource(
          jdbcConfig.url,
          jdbcConfig.user,
          jdbcConfig.password.value
        )
        .group(true)
        .outOfOrder(false)
        .table(config.migrationTable)
        .locations(
          config.migrationLocations
            .map(new Location(_))
            : _*
        )
        .baselineOnMigrate(true)
    )
  }

  private def migrate[F[_] : Sync : MonadThrow](config: FluentConfiguration): F[Int] = {
    Sync[F].delay(
      config.load().migrate().migrationsExecuted
    )
  }
}
