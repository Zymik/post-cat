package ru.kosolapov.ivan.postcat.core.repository.telegram.user

import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.domain.user.UserId
import ru.kosolapov.ivan.postcat.common.dto.CreationStatus

trait TelegramUserRepository[F[_]] {

  /**
   * Insert user with telegram id into database
   * @return
   */
  def insert(user: TelegramUserId): F[CreationStatus]

  /**
   * Check that user contains into database
   * @return true if user exist, false otherwise
   */
  def contains(user: TelegramUserId): F[Boolean]

  /**
   * Get user id by telegram
   * @return [[Some]] with [[UserId]] if user exist, [[None]] otherwise
   */
  def getUserId(user: TelegramUserId): F[Option[UserId]]
}
