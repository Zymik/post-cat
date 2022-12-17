package ru.kosolapov.ivan.postcat.core.config

import cats.effect.Async
import org.typelevel.log4cats.Logger
import ru.kosolapov.ivan.postcat.core.client.telegram.TelegramClient
import ru.kosolapov.ivan.postcat.core.service.group.{GroupService, GroupServiceImpl}
import ru.kosolapov.ivan.postcat.core.service.post.PostService
import ru.kosolapov.ivan.postcat.core.service.post.database.DatabasePostService
import ru.kosolapov.ivan.postcat.core.service.post.telegram.TelegramPostService
import ru.kosolapov.ivan.postcat.core.service.telegram.user.{TelegramUserService, TelegramUserServiceIml}

class ServiceConfiguration[F[_] : Async : Logger]
(
  repositoryConfiguration: RepositoryConfiguration[F],
  telegramClient: TelegramClient[F]
) {

  val postService: PostService[F] = PostService.combinePostServices(
    new TelegramPostService(repositoryConfiguration.telegramChannelGroupRepository, telegramClient),
    new DatabasePostService(repositoryConfiguration.postRepository)
  )

  val telegramUserService: TelegramUserService[F] = new TelegramUserServiceIml[F](
    repositoryConfiguration.telegramUserRepository
  )

  val groupService: GroupService[F] = new GroupServiceImpl[F](
    repositoryConfiguration.groupRepository,
    repositoryConfiguration.telegramChannelGroupRepository
  )
}
