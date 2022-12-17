package ru.kosolapov.ivan.postcat.common.config

import cats.effect.{Async, Resource}
import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor
import ru.kosolapov.ivan.postcat.common.config.database.JdbcConfig

object DatabaseConfiguration {
  /**
   * Configure Postgres transactor with [[JdbcConfig]]
   */
  def getTransactor[F[_] : Async](jdbcConfig: JdbcConfig): Resource[F, HikariTransactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](32)
      xa <- HikariTransactor.newHikariTransactor[F](
        "org.postgresql.Driver",
        jdbcConfig.url,
        jdbcConfig.user,
        jdbcConfig.password.value,
        ce
      )
    } yield xa
}
