package ru.kosolapov.ivan.postcat.core.service.telegram.user

import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.domain.user.User
import ru.kosolapov.ivan.postcat.common.dto.CreationStatus

trait TelegramUserService[F[_]] {

  /**
   * Register telegram user
   * @return [[CreationStatus.Created]] if account was created and
   *         [[CreationStatus.Exist]] if account exist
   */
  def registerUser(telegramUserId: TelegramUserId): F[CreationStatus]

  /**
   * Get user by telegram id
   * @return [[Some]] with [[User]] if user registered and [[None]] if not
   */
  def getUser(telegramUserId: TelegramUserId): F[Option[User]]
}
