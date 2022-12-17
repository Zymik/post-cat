package ru.kosolapov.ivan.postcat.core.config

import cats.effect.Async
import org.http4s.HttpRoutes
import ru.kosolapov.ivan.postcat.core.endpoint.channel.group.GroupEndpoint
import ru.kosolapov.ivan.postcat.core.endpoint.register.TelegramRegisterEndpoint
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

class RoutesConfiguration[F[_] : Async]
(
  controllerConfiguration: ControllerConfiguration[F],
  securityEndpointConfiguration: SecurityEndpointConfiguration[F]
) {


  private val endpoints: List[ServerEndpoint[Fs2Streams[F], F]] =
    List(
      new TelegramRegisterEndpoint[F](controllerConfiguration.telegramRegisterController).endpoints,
      new GroupEndpoint[F](
        securityEndpointConfiguration.telegramUserSecurityEndpoint,
        securityEndpointConfiguration.groupSecurityEndpoint,
        controllerConfiguration.groupController
      ).endpoints
    ).flatten

  private val openApi: List[ServerEndpoint[Any, F]] =
    SwaggerInterpreter()
      .fromEndpoints(endpoints.map(_.endpoint), "Post cat core endpoints", "1.0")

  private val routes: HttpRoutes[F] = Http4sServerInterpreter[F]().toRoutes(List(openApi, endpoints).flatten)
}

object RoutesConfiguration {
  def getRoutes[F[_] : Async](controllerConfiguration: ControllerConfiguration[F],
                              securityEndpointConfiguration: SecurityEndpointConfiguration[F]): HttpRoutes[F] =
    new RoutesConfiguration[F](controllerConfiguration, securityEndpointConfiguration).routes
}
