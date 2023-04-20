package ru.kosolapov.ivan.postcat.common.command.dsl

import cats.syntax.all._

/**
 * @tparam C context
 * @tparam O command result
 */
class Commands[F[_], C, O]
(
  val commands: List[Command[F, C, _, _, O]],
) {
  val description: List[CommandDescription] = commands.map(_.description)

  /**
   * Add command to list
   */
  def |(command: Command[F, C, _, _, O]): Commands[F, C, O] =
    new Commands(command :: commands)

  /**
   * Try to execute command with context and message, that will be parsed to args
   * @return [[Some]] of [[CommandExecution]] if matched some args were parsed and context resolved,
   *         [[None]] if can not parse args from message and resolve context for each command
   */
  def execute(context: C)(message: String): Option[CommandExecution[F, O]] =
    commands
      .collectFirstSome(
        c =>
          c.execute(context)(message)
            .map(CommandExecution(c.description.name, _))
      )

}
