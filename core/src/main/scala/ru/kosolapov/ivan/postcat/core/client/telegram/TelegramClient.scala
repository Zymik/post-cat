package ru.kosolapov.ivan.postcat.core.client.telegram

import ru.kosolapov.ivan.postcat.common.dto.PostStatus
import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramPost}

trait TelegramClient[F[_]] {

  def sendPost(telegramPost: TelegramPost): F[List[(TelegramChannelId, PostStatus)]]

}
