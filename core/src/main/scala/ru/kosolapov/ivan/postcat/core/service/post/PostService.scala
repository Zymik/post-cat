package ru.kosolapov.ivan.postcat.core.service.post


import cats.MonadThrow
import cats.effect.kernel.Concurrent
import cats.syntax.all._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.syntax._
import ru.kosolapov.ivan.postcat.common.dto.PostStatus
import ru.kosolapov.ivan.postcat.common.domain.group.Group
import ru.kosolapov.ivan.postcat.common.domain.post.Post

/**
 * Service to send posts
 */
trait PostService[F[_]] {

  /**
   * Send post to some destination and return sending result in list
   * @return pairs of [[String]] and [[PostStatus]], [[PostStatus.PostSuccess]] if post succeed
   *         and [[PostStatus.PostFailure]] if post failed
   */
  def post(group: Group, post: Post): F[List[(String, PostStatus)]]

}

object PostService {
  /**
   * Combine post service and run them different threads
   */
  def combinePostServices[F[_] : Concurrent : MonadThrow : Logger](seq: PostService[F]*): PostService[F] =
    (group: Group, post: Post) =>
      Concurrent[F].parSequenceN(seq.length)(
        seq.map(
          _.post(group, post)
            .handleErrorWith(
              throwable =>
                for {
                  _ <- error"Unexpected exception while sending posts $throwable"
                } yield Nil
            )
        )
      ).map(_.toList.flatten)

}
