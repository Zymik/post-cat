package ru.kosolapov.ivan.postcat.common.command.dsl

import cats.data.Kleisli
import cats.parse.Parser0

/**
 * Describes command parsing and executable without name
 * @tparam C context
 * @tparam I parsing result
 * @tparam A context resolving result
 * @tparam O command result
 */
case class RawCommand[F[_], C, I, A, O]
(
  parser: Parser0[I],
  contextResolver: Kleisli[Option, C, A],
  executable: A => I => F[O]
)
