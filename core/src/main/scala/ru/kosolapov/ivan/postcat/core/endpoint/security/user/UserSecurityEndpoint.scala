package ru.kosolapov.ivan.postcat.core.endpoint.security.user

import ru.kosolapov.ivan.postcat.common.domain.user.User
import ru.kosolapov.ivan.postcat.common.dto.ApiError
import sttp.tapir.server.PartialServerEndpoint

trait UserSecurityEndpoint[S, F[_]] {
  val securityEndpoint: PartialServerEndpoint[S, User, Unit, ApiError, Unit, Any, F]
}
