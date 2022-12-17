package ru.kosolapov.ivan.postcat.core.config

import cats.effect.Async
import doobie.Transactor
import ru.kosolapov.ivan.postcat.core.repository.group.{DatabaseGroupRepository, GroupRepository}
import ru.kosolapov.ivan.postcat.core.repository.post.{DatabasePostRepository, PostRepository}
import ru.kosolapov.ivan.postcat.core.repository.telegram.group.{DatabaseTelegramChannelGroupRepository, TelegramChannelGroupRepository}
import ru.kosolapov.ivan.postcat.core.repository.telegram.user.{DatabaseTelegramUserRepository, TelegramUserRepository}

class RepositoryConfiguration[F[_] : Async](xa: Transactor[F]) {

  val telegramUserRepository: TelegramUserRepository[F] = new DatabaseTelegramUserRepository[F](xa)

  val groupRepository: GroupRepository[F] = new DatabaseGroupRepository[F](xa)

  val telegramChannelGroupRepository: TelegramChannelGroupRepository[F] = new DatabaseTelegramChannelGroupRepository[F](xa)

  val postRepository: PostRepository[F] = new DatabasePostRepository[F](xa)
}
