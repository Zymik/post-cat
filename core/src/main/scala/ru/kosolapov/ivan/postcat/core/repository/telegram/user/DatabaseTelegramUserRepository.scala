package ru.kosolapov.ivan.postcat.core.repository.telegram.user

import cats.effect.Sync
import cats.syntax.all._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.{Fragment, Transactor}
import ru.kosolapov.ivan.postcat.common.dto.CreationStatus
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.domain.user.UserId

class DatabaseTelegramUserRepository[F[_] : Sync](xa: Transactor[F]) extends TelegramUserRepository[F] {

  override def insert(user: TelegramUserId): F[CreationStatus] = {
    addUserQuery(user).transact(xa).map(CreationStatus.fromCount)
  }

  override def contains(user: TelegramUserId): F[Boolean] =
    containsUserQuery(user).transact(xa).map(_ > 0)

  override def getUserId(user: TelegramUserId): F[Option[UserId]] =
    getUserIdQuery(user).transact(xa)


  private def containsUserQuery(user: TelegramUserId)  =
    sql"SELECT COUNT(*) FROM users WHERE TelegramId = ${user.id}"
      .query[Int]
      .unique

  private def addUserQuery(user: TelegramUserId)  =
    sql"INSERT INTO users (TelegramId) VALUES (${user.id}) ON CONFLICT DO NOTHING"
      .update
      .run

  private def getUserIdQuery(user: TelegramUserId) =
    sql"SELECT UserId FROM users WHERE TelegramId = ${user.id}"
      .query[UserId]
      .option

}
