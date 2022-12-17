package ru.kosolapov.ivan.postcat.api.repository.group

import cats.effect.Async
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor
import ru.kosolapov.ivan.postcat.common.domain.group.GroupId

class DatabaseGroupRepository[F[_] : Async]
(
  xa: Transactor[F]
) extends GroupRepository[F] {

  override def isPublicGroupId(groupId: GroupId): F[Boolean] =
    isPublicGroupIdQuery(groupId)
      .transact(xa)

  private def isPublicGroupIdQuery(groupId: GroupId) =
    sql"SELECT (RestApiPublicity) FROM Groups WHERE GroupId = $groupId"
      .query[Boolean]
      .unique

}
