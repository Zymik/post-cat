package ru.kosolapov.ivan.postcat.common.command.dsl

/**
 * Command with name and prepared execution
 */
case class CommandExecution[F[_], O](name: String, execution: F[O])
