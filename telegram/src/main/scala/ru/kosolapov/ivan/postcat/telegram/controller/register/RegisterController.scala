package ru.kosolapov.ivan.postcat.telegram.controller.register

import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId

trait RegisterController[F[_]] {

  /**
   * Register user in post app
   */
  def register(userId: TelegramUserId): F[String]

}
