package ru.kosolapov.ivan.postcat.common.command


import cats.data.{Kleisli, NonEmptyList}
import cats.parse.{Parser, Parser0}

/**
 * DSL for writing bot commands, [[Command]] represent command. [[Commands]] represent banch of commands.
 * Command should have format:
 * {{{
 *   name / description / argsParser [~| contextResolver] ~> executable
 * }}}
 *
 * context resolver can by inserted implicitly.
 *
 * name, description - displayed name.
 * parser - used to parse executable args.
 *
 * Parser of name prepends to parser of args. Final parser of command will be
 * {{{Parser.string(description.name) *> Parser.charsWhile0(_.isWhitespace) *> argsParser}}}
 *
 * contextResolver - used to resolved extra context from message. Like user id or message id.
 * Have type [[Kleisli]][Option, C, A] because resolving of context can fail.
 * [[Kleisli]] use to have possibility to use cats combinators.
 *
 * executable - executes when args parsed and context resolved
 */
package object dsl {

  implicit class commandName(val name: String) {

    /**
     * Create [[CommandDescription]] from name and description parser description
     */
    def /(description: String): CommandDescription = CommandDescription(name, description)
  }

  /**
   *
   * @tparam I parsing result
   */
  implicit class parserExecute[I](val parser: Parser0[I]) {

    /**
     * Add context resolver to parser
     * @tparam C context
     * @tparam A context resolving result
     */
    def ~|[C, A](contextResolver: Kleisli[Option, C, A]): ParserWithContextResolver[C, I, A] = ParserWithContextResolver(
      parser,
      contextResolver
    )

    /**
     * Takes executable explicitly and context resolver implicitly and creates [[RawCommand]]
     * @tparam C context
     * @tparam A context resolving result
     * @tparam O command result
     */
    def ~>[C, A, O, F[_]](executable: A => I => F[O])(implicit contextResolver: Kleisli[Option, C, A]): RawCommand[F, C, I, A, O] =
      RawCommand(
        parser,
        contextResolver,
        executable
      )
  }


  /**
   * String argument surrounded by whitespace
   */
  implicit val stringArg: Parser[String] =
    Parser.charsWhile(!_.isWhitespace) surroundedBy Parser.charsWhile0(_.isWhitespace)

  /**
   * List og string args
   */
  implicit val stringList: Parser[NonEmptyList[String]] = stringArg.rep

  /**
   * Empty arg
   */
  implicit val noArg: Parser0[Unit] = Parser.unit

  /**
   * Implicit resolver parser for arg
   */
  def arg[A](implicit parser: Parser0[A]): Parser0[A] = parser

}
