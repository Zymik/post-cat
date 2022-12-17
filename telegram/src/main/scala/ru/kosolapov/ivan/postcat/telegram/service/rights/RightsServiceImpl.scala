package ru.kosolapov.ivan.postcat.telegram.service.rights

import cats.Functor
import cats.syntax.all._
import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramUserId}
import ru.kosolapov.ivan.postcat.telegram.client.ChatClient
import ru.kosolapov.ivan.postcat.telegram.implicits.ChatMemberExt

class RightsServiceImpl[F[_] : Functor]
(
  chatClient: ChatClient[F]
)
extends RightsService[F] {
  /**
   * Check that user can post to chat
   *
   * @return true if can post, else false
   */
  override def canPost(channelId: TelegramChannelId, userId: TelegramUserId): F[Boolean] =
    chatClient.getMember(channelId, userId)
      .map(_.canPost)

  /**
   * Check that bot can post to channel
   *
   * @return true if can, else false
   */
  override def canBotPost(channelId: TelegramChannelId): F[Boolean] =
    chatClient.getBotInChat(channelId)
      .map(_.canPost)
}
