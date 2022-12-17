package ru.kosolapov.ivan.postcat.core.config

import cats.Monad
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.core.endpoint.security.channel.group.{GroupSecurityEndpoint, TelegramGroupSecurityEndpoint}
import ru.kosolapov.ivan.postcat.core.endpoint.security.user.{TelegramUserSecurityEndpoint, UserSecurityEndpoint}

class SecurityEndpointConfiguration[F[_] : Monad]
(
  serviceConfiguration: ServiceConfiguration[F]
) {

  val telegramUserSecurityEndpoint: UserSecurityEndpoint[TelegramUserId, F]
    = new TelegramUserSecurityEndpoint[F](serviceConfiguration.telegramUserService)

  val groupSecurityEndpoint: GroupSecurityEndpoint[(TelegramUserId, String), F]  = new TelegramGroupSecurityEndpoint[F](
    serviceConfiguration.telegramUserService,
    serviceConfiguration.groupService
  )

}
