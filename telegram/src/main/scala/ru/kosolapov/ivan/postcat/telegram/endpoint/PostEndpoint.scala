package ru.kosolapov.ivan.postcat.telegram.endpoint

import io.circe.generic.auto._
import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramPost}
import ru.kosolapov.ivan.postcat.common.dto.PostStatus
import ru.kosolapov.ivan.postcat.telegram.controller.post.PostController
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint

/**
 *
 */
class PostEndpoint[F[_]]
(
  postController: PostController[F]
) {
  val postEndpoint: ServerEndpoint[Any, F] = endpoint
    .post
    .in("channel" / "post")
    .in(jsonBody[TelegramPost])
    .out(jsonBody[List[(TelegramChannelId, PostStatus)]])
    .serverLogicSuccess(post => postController.sendPost(post))
}
