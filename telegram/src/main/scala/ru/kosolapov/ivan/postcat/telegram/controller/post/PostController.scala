package ru.kosolapov.ivan.postcat.telegram.controller.post

import ru.kosolapov.ivan.postcat.common.dto.PostStatus
import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramPost}

/**
 * All methods returns [[Some]] with message to answer or None if no message to answer
 */
trait PostController[F[_]] {

  /**
   * Try to send post to all channels specified with it
   * @return List with status of post. [[PostStatus.PostSuccess]] if post was posted else [[PostStatus.PostFailure]]
   */
  def sendPost(post: TelegramPost): F[List[(TelegramChannelId, PostStatus)]]

}
