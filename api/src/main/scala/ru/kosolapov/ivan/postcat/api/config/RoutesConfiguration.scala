package ru.kosolapov.ivan.postcat.api.config

import cats.effect.Async
import org.http4s.HttpRoutes
import ru.kosolapov.ivan.postcat.api.endpoint.post.PostEndpoint
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

class RoutesConfiguration[F[_] : Async]
(
  controllerConfiguration: ControllerConfiguration[F],
  serviceConfiguration: ServiceConfiguration[F]
) {

  private val endpoints: List[ServerEndpoint[Fs2Streams[F], F]] =
    List(
      new PostEndpoint(
        controllerConfiguration.postController,
        serviceConfiguration.groupValidationService
      ).getPostsEndpoint
    )

  private val openApi: List[ServerEndpoint[Any, F]] =
    SwaggerInterpreter()
      .fromEndpoints(endpoints.map(_.endpoint), "Post cat core endpoints", "1.0")

  private val routes: HttpRoutes[F] = Http4sServerInterpreter[F]().toRoutes(List(openApi, endpoints).flatten)

}

object RoutesConfiguration {
  def getRoutes[F[_] : Async](controllerConfiguration: ControllerConfiguration[F],
                              serviceConfiguration: ServiceConfiguration[F]): HttpRoutes[F] =
    new RoutesConfiguration(
      controllerConfiguration,
      serviceConfiguration
    ).routes
}
