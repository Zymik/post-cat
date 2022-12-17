package ru.kosolapov.ivan.postcat.core.repository.post
import cats.effect.Async
import doobie.util.transactor.Transactor
import doobie.implicits._
import doobie.postgres.implicits._
import cats.syntax.all._
import ru.kosolapov.ivan.postcat.common.domain.group.GroupId
import ru.kosolapov.ivan.postcat.common.domain.post.Post

class DatabasePostRepository[F[_] : Async](xa: Transactor[F]) extends PostRepository[F] {

  override def addPost(groupId: GroupId, post: Post): F[Unit] =
    addPostQuery(groupId, post)
      .transact(xa)
      .map(_ => ())


  private def addPostQuery(groupId: GroupId, post: Post) =
    sql"INSERT INTO GroupPosts (GroupId, PostText) VALUES ($groupId, $post)"
      .update
      .run
}
