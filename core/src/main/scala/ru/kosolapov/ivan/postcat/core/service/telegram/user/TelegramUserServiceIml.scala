package ru.kosolapov.ivan.postcat.core.service.telegram.user

import cats.Monad
import cats.syntax.all._
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.domain.user.User
import ru.kosolapov.ivan.postcat.common.dto.CreationStatus
import ru.kosolapov.ivan.postcat.core.repository.telegram.user.TelegramUserRepository

class TelegramUserServiceIml[F[_] : Monad]
(
  telegramUsersRepository: TelegramUserRepository[F]
) extends TelegramUserService[F] {

  override def registerUser(telegramUserId: TelegramUserId): F[CreationStatus] = {
    for {
      contains <- telegramUsersRepository.contains(telegramUserId)
      status <-
        if (contains)
          Monad[F].pure(CreationStatus.Exist)
        else
          telegramUsersRepository.insert(telegramUserId)
    } yield status
  }

  override def getUser(telegramUserId: TelegramUserId): F[Option[User]] =
    telegramUsersRepository
      .getUserId(telegramUserId)
      .map(
        _.map(User(_, telegramUserId))
      )
}
