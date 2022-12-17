package ru.kosolapov.ivan.postcat.telegram.controller.post

import cats.MonadThrow
import cats.syntax.all._
import org.typelevel.log4cats.Logger
import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramPost}
import ru.kosolapov.ivan.postcat.common.dto.PostStatus
import ru.kosolapov.ivan.postcat.common.implicits.MonadThrowLogger
import ru.kosolapov.ivan.postcat.telegram.service.post.PostService

class PostControllerImpl[F[_] : MonadThrow : Logger](postService: PostService[F]) extends PostController[F] {

  override def sendPost(post: TelegramPost): F[List[(TelegramChannelId, PostStatus)]] = {
    post.channelIds.map(
      channel =>
        postService
          .postToChannel(post.authorId, channel, post.post.text)
          .logErrorHandle(
            s"Unexpected exception while posting to channel $channel",
            _ => PostStatus.PostFailure(PostStatus.Unexpected)
          )
          .map((channel, _))
    ).sequence

  }

}
