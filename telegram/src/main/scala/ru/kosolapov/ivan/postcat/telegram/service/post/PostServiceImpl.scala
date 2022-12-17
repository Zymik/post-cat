package ru.kosolapov.ivan.postcat.telegram.service.post

import cats.Monad
import cats.syntax.all._
import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramUserId}
import ru.kosolapov.ivan.postcat.common.dto.PostStatus
import ru.kosolapov.ivan.postcat.common.dto.PostStatus.{BotNoRightsToPost, PostFailure, PostSuccess, UserNoRightsToPost}
import ru.kosolapov.ivan.postcat.telegram.client.ChatClient
import ru.kosolapov.ivan.postcat.telegram.service.rights.RightsService

class PostServiceImpl[F[_] : Monad]
(
  rightsService: RightsService[F],
  chatClient: ChatClient[F]
) extends PostService[F] {

  override def postToChannel(authorId: TelegramUserId, channelId: TelegramChannelId, text: String): F[PostStatus] = {
    rightsService.canBotPost(channelId)
      .ifM(
        rightsService.canPost(channelId, authorId)
          .ifM(
            chatClient.sendMessage(channelId, text).pure >> (PostSuccess: PostStatus).pure,
            (PostFailure(UserNoRightsToPost): PostStatus).pure
          ),
        (PostFailure(BotNoRightsToPost): PostStatus).pure
      )
  }
}
