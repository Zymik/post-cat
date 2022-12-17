package ru.kosolapov.ivan.postcat.telegram.config

import cats.effect.Async
import org.http4s.HttpRoutes
import ru.kosolapov.ivan.postcat.telegram.endpoint.PostEndpoint
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter


class RoutesConfiguration[F[_] : Async](controllerConfiguration: ControllerConfiguration[F]) {
  private val endpoints: List[ServerEndpoint[Fs2Streams[F], F]] =
    List(new PostEndpoint(controllerConfiguration.postController).postEndpoint)

  private val openApi: List[ServerEndpoint[Any, F]] =
    SwaggerInterpreter()
      .fromEndpoints(endpoints.map(_.endpoint), "Post cat telegram endpoints", "1.0")

  private val routes: HttpRoutes[F] = Http4sServerInterpreter[F]().toRoutes(List(openApi, endpoints).flatten)
}

object RoutesConfiguration {
  def getRoutes[F[_] : Async](controllerConfiguration: ControllerConfiguration[F]) : HttpRoutes[F] =
    new RoutesConfiguration(controllerConfiguration).routes
}
