package ru.kosolapov.ivan.postcat.core.repository.group

import cats.effect.Async
import cats.syntax.all._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor
import ru.kosolapov.ivan.postcat.common.dto.CreationStatus
import ru.kosolapov.ivan.postcat.common.domain.group.GroupId
import ru.kosolapov.ivan.postcat.common.domain.user.UserId


class DatabaseGroupRepository[F[_] : Async](xa: Transactor[F]) extends GroupRepository[F] {

  override def createGroup(ownerId: UserId, groupName: String): F[CreationStatus] =
    createChannelGroupQuery(ownerId, groupName)
      .transact(xa)
      .map(CreationStatus.fromCount)

  override def getGroupId(ownerId: UserId, groupName: String): F[Option[GroupId]] =
    getGroupQuery(ownerId, groupName)
      .transact(xa)

  override def setRestApiPublicity(groupId: GroupId, publicity: Boolean): F[Unit] =
    setRestApiPublicityQuery(groupId, publicity)
      .transact(xa)
      .map(_ => ())

  override def contains(ownerId: UserId, groupName: String): F[Boolean] =
    containsQuery(ownerId, groupName)
      .transact(xa)

  private def createChannelGroupQuery(user: UserId, name: String) =
    sql"INSERT INTO Groups (OwnerId, GroupName) VALUES (${user.uuid}, $name) ON CONFLICT DO NOTHING"
      .update
      .run

  private def getGroupQuery(user: UserId, name: String) =
    sql"SELECT GroupId FROM Groups WHERE OwnerId=$user AND GroupName=$name"
      .query[GroupId]
      .option

  private def setRestApiPublicityQuery(groupId: GroupId, publicity: Boolean) =
    sql"UPDATE Groups SET RestApiPublicity=$publicity WHERE GroupId=$groupId"
      .update
      .run

  private def containsQuery(ownerId: UserId, groupName: String) =
    sql"SELECT COUNT(*) FROM GROUPS WHERE OwnerId = $ownerId AND GroupName = $groupName"
      .query[Int]
      .map(_ > 0)
      .unique

}
