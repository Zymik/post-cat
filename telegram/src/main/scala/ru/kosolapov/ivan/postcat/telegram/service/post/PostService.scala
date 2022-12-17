package ru.kosolapov.ivan.postcat.telegram.service.post

import ru.kosolapov.ivan.postcat.common.dto.PostStatus
import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramUserId}

trait PostService[F[_]] {
  /**
   * Send post to channel
   * @param authorId id of author
   * @param text post text
   * @param channelId id of channel
   * @return [[PostStatus.PostSuccess]] if message was posted else [[PostStatus.PostFailure]]
   */
  def postToChannel(authorId: TelegramUserId, channelId: TelegramChannelId, text: String): F[PostStatus]

}
