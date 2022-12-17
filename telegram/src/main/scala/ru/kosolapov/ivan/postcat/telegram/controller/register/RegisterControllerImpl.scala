package ru.kosolapov.ivan.postcat.telegram.controller.register

import cats.Functor
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.dto.CreationStatus._
import ru.kosolapov.ivan.postcat.common.implicits.EitherApiError
import ru.kosolapov.ivan.postcat.telegram.service.register.RegisterService

class RegisterControllerImpl[F[_] : Functor]
(
  registerService: RegisterService[F]
) extends RegisterController[F] {

  override def register(userId: TelegramUserId): F[String] =
    registerService
      .registerUser(userId)
      .map {
        case Exist => "You are already registered"
        case Created => "You were successfully registered!"
      }
      .errorToString

}
