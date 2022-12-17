package ru.kosolapov.ivan.postcat.telegram.config

import cats.Monad
import ru.kosolapov.ivan.postcat.telegram.bot.BotCommands

/**
 * Configuration of bot commands with [[ru.kosolapov.ivan.postcat.common.command.dsl]]
 */
class BotCommandsConfiguration[F[_] : Monad]
(
  controllerConfiguration: ControllerConfiguration[F]
) {

  val commands: BotCommands[F] = new BotCommands(
    controllerConfiguration.groupController,
    controllerConfiguration.registerController
  )

}
