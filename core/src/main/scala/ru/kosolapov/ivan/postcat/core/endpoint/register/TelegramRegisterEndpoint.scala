package ru.kosolapov.ivan.postcat.core.endpoint.register

import cats.Monad
import io.circe.generic.auto._
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.dto.CreationStatus
import ru.kosolapov.ivan.postcat.core.controller.telegram.register.TelegramRegisterController
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint

class TelegramRegisterEndpoint[F[_] : Monad]
(
  controller: TelegramRegisterController[F]
)
{

  private val registerEndpoint: ServerEndpoint[Any, F] =
    endpoint
      .post
      .description("Resister telegram user for creating post channels")
      .in("register" / "telegram")
      .in(jsonBody[TelegramUserId])
      .out(jsonBody[CreationStatus])
      .serverLogicSuccess(controller.register)

  val endpoints: List[ServerEndpoint[Fs2Streams[F], F]] = List(registerEndpoint)
}
