package ru.kosolapov.ivan.postcat.common.config.database

case class FlywayConfig
(
  jdbcConfig: JdbcConfig,
  migrationTable: String,
  migrationLocations: List[String]
)
