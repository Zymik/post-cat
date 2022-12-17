package ru.kosolapov.ivan.postcat.core.endpoint.security.channel.group

import cats.Monad
import cats.data.OptionT
import cats.syntax.all._
import io.circe.generic.auto._
import ru.kosolapov.ivan.postcat.common.domain.group.Group
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.dto.ApiError
import ru.kosolapov.ivan.postcat.common.endpoint.Headers
import ru.kosolapov.ivan.postcat.core.service.group.GroupService
import ru.kosolapov.ivan.postcat.core.service.telegram.user.TelegramUserService
import sttp.tapir
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.PartialServerEndpoint
import sttp.tapir.{Validator, header}

class TelegramGroupSecurityEndpoint[F[_] : Monad]
(
  telegramUserService: TelegramUserService[F],
  groupService: GroupService[F]
) extends GroupSecurityEndpoint[(TelegramUserId, String), F] {

  override val endpoint:
    PartialServerEndpoint[(TelegramUserId, String), Group, Unit, ApiError, Unit, Any, F] =
    tapir.endpoint
      .errorOut(jsonBody[ApiError])
      .securityIn(
        header[Long](Headers.user)
          .map(TelegramUserId)(_.id)
          .and(
            header[String](Headers.groupName)
              .validate(Validator.nonEmptyString)
          )
      )
      .serverSecurityLogic {
        case (telegramUserId, name) =>
          val group = for {
            user <- OptionT(telegramUserService.getUser(telegramUserId))
            group <- OptionT(groupService.getGroup(user, name))
          } yield group

          val t: F[Either[ApiError, Group]] = group.value.map {
            case Some(group) => Right(group)
            case None => Left(ApiError(s"No such group"))
          }
          t
      }

}
