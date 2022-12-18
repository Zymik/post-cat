package ru.kosolapov.ivan.postcat.telegram.config

import cats.effect.kernel.{Concurrent, MonadCancelThrow}
import org.http4s.client.Client
import org.http4s.{Method, Request, Uri}
import org.typelevel.log4cats.Logger
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.telegram.client.{ChatClient, ChatClientImpl}
import ru.kosolapov.ivan.postcat.telegram.service.channel.group.{GroupService, GroupServiceImpl}
import ru.kosolapov.ivan.postcat.telegram.service.post.{PostService, PostServiceImpl}
import ru.kosolapov.ivan.postcat.telegram.service.register.{RegisterService, RegisterServiceImpl}
import ru.kosolapov.ivan.postcat.telegram.service.rights.{RightsService, RightsServiceImpl}
import telegramium.bots.high.Api


class ServiceConfiguration[F[_] : MonadCancelThrow : Concurrent : Logger]
(
  httpClient: Client[F],
  chatClient: ChatClient[F],
  coreUri: Uri
) {

  private val baseRequest = Request[F](method = Method.POST, uri = coreUri)
  val groupService: GroupService[F] = new GroupServiceImpl[F](
    httpClient,
    baseRequest
      .withUri(
        baseRequest.uri / "group"
      )
  )

  val registerService: RegisterService[F] = new RegisterServiceImpl[F](
    httpClient,
    baseRequest
      .withUri(
        baseRequest.uri / "register" / "telegram"
      )
  )

  val rightsService: RightsService[F] = new RightsServiceImpl[F](chatClient)

  val postService: PostService[F] = new PostServiceImpl[F](
    rightsService,
    chatClient
  )
}
