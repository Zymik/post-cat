package ru.kosolapov.ivan.postcat.core.endpoint.security.channel.group

import ru.kosolapov.ivan.postcat.common.domain.group.Group
import ru.kosolapov.ivan.postcat.common.dto.ApiError
import sttp.tapir.server.PartialServerEndpoint

trait GroupSecurityEndpoint[I, F[_]] {

  val endpoint: PartialServerEndpoint[I, Group, Unit, ApiError, Unit, Any, F]

}
