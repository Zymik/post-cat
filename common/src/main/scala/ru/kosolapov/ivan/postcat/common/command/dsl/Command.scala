package ru.kosolapov.ivan.postcat.common.command.dsl

import cats.data.Kleisli
import cats.parse.{Parser, Parser0}

import scala.language.implicitConversions

/**
 * Command with description and executable part
 *
 * @tparam C context
 * @tparam I parsing result
 * @tparam A context resolving result
 * @tparam O command execution result
 */
class Command[F[_], C, I, A, O]
(
  val description: CommandDescription,
  argsParser: Parser0[I],
  contextResolver: Kleisli[Option, C, A],
  executable: A => I => F[O]
) {

  /**
   * Total command parser
   */
  private val parser: Parser[I] =
    Parser.string(description.name) *> (Parser.charsWhile0(_.isWhitespace) *> argsParser)

  /**
   * Creates [[Commands]] from two [[Command]]
   */
  def |(command: Command[F, C, _, _, O]): Commands[F, C, O] =
    new Commands(List(this, command))


  /**
   * Try parse args and resolve context
   *
   * @return Non if can't parse args from message or resolve context
   */
  def execute(context: C)(message: String): Option[F[O]] =
    for {
      resolvedContext <- contextResolver(context)
      args <- parser.parse(message).toOption.map(_._2)
    } yield
      executable(resolvedContext)(args)

}

object Command {
  implicit def toCommands[F[_], C, I, A, O](command: Command[F, C, I, A, O]): Commands[F, C, O] =
    new Commands(List(command))
}