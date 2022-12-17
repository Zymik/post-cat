package ru.kosolapov.ivan.postcat.common.command.dsl

/**
 * Command with name and display description
 */
case class CommandDescription(name: String, description: String) {

  /**
   * Creates [[Command]] from [[CommandDescription]] and [[RawCommand]]
   * @tparam C context
   * @tparam I parsing result
   * @tparam A context resolving result
   * @tparam O command execution result
   */
  def /[F[_], C, I, A, O](command: RawCommand[F, C, I, A, O]): Command[F, C, I, A, O] =
    new Command(this, command.parser, command.contextResolver, command.executable)



}
