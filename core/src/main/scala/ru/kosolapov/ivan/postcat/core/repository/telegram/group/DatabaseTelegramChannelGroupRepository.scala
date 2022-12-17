package ru.kosolapov.ivan.postcat.core.repository.telegram.group

import cats.effect.Async
import cats.syntax.all._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor
import doobie.util.update.Update
import ru.kosolapov.ivan.postcat.common.domain.group.GroupId
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramChannelId


class DatabaseTelegramChannelGroupRepository[F[_] : Async](xa: Transactor[F]) extends TelegramChannelGroupRepository[F] {

  override def addChannels(group: GroupId, channels: Set[TelegramChannelId]): F[Unit] =
    Update[(Long, String)](insertGroupWithChannelQuery)
      .updateMany(
        List.fill(channels.size)(group.id)
          .zip(channels.map(_.id))
      )
      .transact(xa).map(_ => ())

  override def getTelegramChannelsByGroup(groupId: GroupId): F[List[TelegramChannelId]] =
    getTelegramChannelsQuery(groupId).transact(xa)


  private def getTelegramChannelsQuery(groupId: GroupId) =
    sql"SELECT (TelegramChannelId) FROM GroupToTelegramChannel WHERE GroupId=$groupId"
      .query[TelegramChannelId]
      .to[List]

  private val insertGroupWithChannelQuery =
    "INSERT INTO GroupToTelegramChannel (GroupId, TelegramChannelId) VALUES (?, ?) ON CONFLICT DO NOTHING"
}
