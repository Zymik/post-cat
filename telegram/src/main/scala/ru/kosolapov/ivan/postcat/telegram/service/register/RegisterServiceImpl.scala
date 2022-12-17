package ru.kosolapov.ivan.postcat.telegram.service.register

import cats.data.EitherT
import cats.effect.kernel.{Concurrent, MonadCancelThrow}
import io.circe.generic.auto._
import org.http4s.circe.jsonEncoderOf
import org.http4s.client.Client
import org.http4s.{EntityBody, Request}
import org.typelevel.log4cats.Logger
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.dto.{ApiError, CreationStatus}
import ru.kosolapov.ivan.postcat.common.implicits.ClientWithApiError

import scala.language.postfixOps

class RegisterServiceImpl[F[_] : MonadCancelThrow : Concurrent : Logger]
(
  httpClient: Client[F],
  baseRequest: Request[F]
) extends RegisterService[F] {

  override def registerUser(userId: TelegramUserId): EitherT[F, ApiError, CreationStatus] = {
    val request = baseRequest.withBodyStream(encode(userId))
    httpClient
      .runWithDecode(request)
  }


  private def encode(user: TelegramUserId): EntityBody[F] = {
    jsonEncoderOf[F, TelegramUserId].toEntity(user).body
  }

}
