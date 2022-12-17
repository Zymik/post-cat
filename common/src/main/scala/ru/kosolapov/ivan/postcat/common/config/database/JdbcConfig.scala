package ru.kosolapov.ivan.postcat.common.config.database

import ciris.Secret

case class JdbcConfig
(
  user: String,
  password: Secret[String],
  url: String
)
