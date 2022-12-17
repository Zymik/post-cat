package ru.kosolapov.ivan.postcat.core.service.post.telegram

import cats.Monad
import cats.syntax.all._
import ru.kosolapov.ivan.postcat.common.domain.group.Group
import ru.kosolapov.ivan.postcat.common.domain.post.Post
import ru.kosolapov.ivan.postcat.common.domain.telegram
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramPost
import ru.kosolapov.ivan.postcat.common.dto.PostStatus
import ru.kosolapov.ivan.postcat.core.client.telegram.TelegramClient
import ru.kosolapov.ivan.postcat.core.repository.telegram.group.TelegramChannelGroupRepository
import ru.kosolapov.ivan.postcat.core.service.post.PostService

/**
 * Service that sends posts to telegram channels associated with group
 */
class TelegramPostService[F[_] : Monad]
(
  telegramChannelGroupRepository: TelegramChannelGroupRepository[F],
  telegramClient: TelegramClient[F]
) extends PostService[F] {

  override def post(channelGroup: Group, post: Post): F[List[(String, PostStatus)]] =
    telegramChannelGroupRepository
      .getTelegramChannelsByGroup(channelGroup.groupId)
      .flatMap(
        channels =>
          telegramClient.sendPost(
            telegram.TelegramPost(channelGroup.owner.telegramUserId, post, channels)
          )
            .map(
              list => list.map(p => (p._1.id, p._2))
            )
      )

}
