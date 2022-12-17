package ru.kosolapov.ivan.postcat.api.repository.post

import cats.effect.Async
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor
import ru.kosolapov.ivan.postcat.common.domain.group.GroupId
import ru.kosolapov.ivan.postcat.common.domain.post.Post

class DatabasePostRepository[F[_] : Async]
(
  xa: Transactor[F]
) extends PostRepository[F] {

  override def getPosts(groupId: GroupId, count: Int, higherId: Long): F[List[Post]] =
    getPostsQuery(groupId, higherId, count)
      .transact(xa)

  override def getLastPosts(groupId: GroupId, count: Int): F[List[Post]] =
    getLastPostsQuery(groupId, count)
      .transact(xa)


  private def getLastPostsQuery(groupId: GroupId, limit: Int) =
    sql"SELECT (PostText) FROM GroupPosts WHERE GroupId = $groupId ORDER BY PostId DESC LIMIT $limit"
      .query[Post]
      .to[List]


  private def getPostsQuery(groupId: GroupId, higherId: Long, limit: Int) =
    sql"SELECT (PostText) FROM GroupPosts WHERE GroupId = $groupId AND PostId < $higherId ORDER BY PostId DESC LIMIT $limit"
      .query[Post]
      .to[List]
}
