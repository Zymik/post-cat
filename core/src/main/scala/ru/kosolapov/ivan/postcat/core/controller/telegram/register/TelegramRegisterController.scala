package ru.kosolapov.ivan.postcat.core.controller.telegram.register

import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.dto.CreationStatus


trait TelegramRegisterController[F[_]] {
  /**
   * Register user in post app
   * @return [[CreationStatus]] as result of registration
   */
  def register(user: TelegramUserId): F[CreationStatus]
}
