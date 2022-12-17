package ru.kosolapov.ivan.postcat.core.service.post.database

import cats.MonadThrow
import org.typelevel.log4cats.Logger
import ru.kosolapov.ivan.postcat.common.implicits.MonadThrowLogger
import ru.kosolapov.ivan.postcat.core.repository.post.PostRepository
import ru.kosolapov.ivan.postcat.core.service.post.PostService
import cats.syntax.all._
import ru.kosolapov.ivan.postcat.common.domain.group.Group
import ru.kosolapov.ivan.postcat.common.domain.post.Post
import ru.kosolapov.ivan.postcat.common.dto.PostStatus
import ru.kosolapov.ivan.postcat.common.dto.PostStatus.Unexpected

/**
 * Service that sends post to database
 */
class DatabasePostService[F[_] : MonadThrow : Logger]
(
  postRepository: PostRepository[F]
) extends PostService[F] {

  private val displayName = "Posts storage"

  override def post(group: Group, post: Post): F[List[(String, PostStatus)]] =
    postRepository
      .addPost(group.groupId, post)
      .map(_ => List((displayName, PostStatus.PostSuccess : PostStatus)))
      .logErrorHandle(
        s"Unexpected error while adding post to posts database for group $group",
        _ => List((displayName, PostStatus.PostFailure(Unexpected)))
      )

}
