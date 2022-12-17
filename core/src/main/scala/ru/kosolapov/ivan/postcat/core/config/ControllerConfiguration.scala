package ru.kosolapov.ivan.postcat.core.config

import cats.effect.Async
import ru.kosolapov.ivan.postcat.core.controller.group.{GroupController, GroupControllerImpl}
import ru.kosolapov.ivan.postcat.core.controller.telegram.register.{TelegramRegisterController, TelegramRegisterControllerImpl}

class ControllerConfiguration[F[_] : Async](serviceConfiguration: ServiceConfiguration[F]) {

  val telegramRegisterController: TelegramRegisterController[F] =
    new TelegramRegisterControllerImpl[F](
      serviceConfiguration.telegramUserService
    )

  val groupController: GroupController[F] =
    new GroupControllerImpl[F](
      serviceConfiguration.groupService,
      serviceConfiguration.postService
    )

}
