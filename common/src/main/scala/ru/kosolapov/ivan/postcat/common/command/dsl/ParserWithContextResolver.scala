package ru.kosolapov.ivan.postcat.common.command.dsl

import cats.data.Kleisli
import cats.parse.Parser0

/**
 * Parser and context resolver for command
 * @tparam C context
 * @tparam I parsing result
 * @tparam A context resolving result
 */
case class ParserWithContextResolver[C, I, A](parser: Parser0[I], contextResolver: Kleisli[Option, C, A]) {

  /**
   * Adding executable part to command
   * @param executable command executable part
   * @return
   */
  def ~>[F[_], O](executable: A => I => F[O]) = new RawCommand[F, C, I, A, O](
    parser,
    contextResolver,
    executable
  )
}
