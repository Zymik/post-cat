package ru.kosolapov.ivan.postcat.core.controller.telegram.register

import cats.Functor
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.dto.CreationStatus
import ru.kosolapov.ivan.postcat.core.service.telegram.user.TelegramUserService

class TelegramRegisterControllerImpl[F[_] : Functor](registerService: TelegramUserService[F]) extends TelegramRegisterController[F] {
  override def register(user: TelegramUserId): F[CreationStatus] =
    registerService
      .registerUser(user)
}
