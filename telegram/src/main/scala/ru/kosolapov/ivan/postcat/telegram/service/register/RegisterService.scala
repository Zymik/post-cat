package ru.kosolapov.ivan.postcat.telegram.service.register

import cats.data.EitherT
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.dto.{ApiError, CreationStatus}

trait RegisterService[F[_]] {

 /**
   * Register user in post app. If user already registered do nothing
   * @return [[CreationStatus.Created]] if user was registered,
   *         [[CreationStatus.Exist]] if registration was already performed
   */
  def registerUser(userId: TelegramUserId): EitherT[F, ApiError, CreationStatus]
}
