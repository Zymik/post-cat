package ru.kosolapov.ivan.postcat.core.endpoint.security.user

import cats.Functor
import cats.syntax.all._
import ru.kosolapov.ivan.postcat.common.endpoint.Headers
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.generic.auto._
import io.circe.generic.auto._
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.domain.user.User
import ru.kosolapov.ivan.postcat.common.dto.ApiError
import ru.kosolapov.ivan.postcat.core.service.telegram.user.TelegramUserService
import sttp.tapir.server.PartialServerEndpoint

class TelegramUserSecurityEndpoint[F[_] : Functor]
(
  telegramUserService: TelegramUserService[F]
) extends UserSecurityEndpoint[TelegramUserId, F] {

  val securityEndpoint: PartialServerEndpoint[TelegramUserId, User, Unit, ApiError, Unit, Any, F] =
    endpoint
      .errorOut(jsonBody[ApiError])
      .securityIn(header[Long](Headers.user).map[TelegramUserId](TelegramUserId)(_.id))
      .serverSecurityLogic(
        telegramUserId =>
          telegramUserService
            .getUser(telegramUserId).map {
            case Some(user) => Right(user)
            case None => Left(ApiError(s"Not registered"))
          }
      )

}
