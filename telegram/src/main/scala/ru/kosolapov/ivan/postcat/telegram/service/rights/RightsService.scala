package ru.kosolapov.ivan.postcat.telegram.service.rights

import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramUserId}
import telegramium.bots.ChatId

trait RightsService[F[_]] {

  /**
   * Check that user can post to chat
   * @return true if can post, else false
   */
  def canPost(channelId: TelegramChannelId, userId: TelegramUserId): F[Boolean]

  /**
   * Check that bot can post to channel
   * @return true if can, else false
   */
  def canBotPost(channelId: TelegramChannelId) : F[Boolean]

}
