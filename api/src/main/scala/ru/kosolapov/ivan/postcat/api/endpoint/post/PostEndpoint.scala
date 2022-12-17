package ru.kosolapov.ivan.postcat.api.endpoint.post

import io.circe.generic.auto._
import ru.kosolapov.ivan.postcat.common.domain.group.GroupId
import ru.kosolapov.ivan.postcat.common.domain.post.Post
import ru.kosolapov.ivan.postcat.common.dto.ApiError
import ru.kosolapov.ivan.postcat.api.controller.post.PostController
import ru.kosolapov.ivan.postcat.api.service.group.GroupValidationService
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint

/**
 * Description getting post endpoint
 */
class PostEndpoint[F[_]]
(
  postController: PostController[F],
  groupValidationService: GroupValidationService[F]
) {

  private val getPostsParams: EndpointInput[(Int, Option[Long])] = query[Int]("count")
    .default(10)
    .validate(Validator.min(1))
    .validate(Validator.max(100))
    .and(
      query[Option[Long]]("higherNumber")
        .validateOption(Validator.min[Long](1))
    )

  val getPostsEndpoint: ServerEndpoint[Any, F] =
    endpoint
      .get
      .description("Get count posts from group with ids that lower than higher")
      .securityIn(
        path[Long].mapTo[GroupId]
      )
      .in("post" / "get")
      .in(getPostsParams)
      .out(jsonBody[List[Post]])
      .errorOut(jsonBody[ApiError])
      .serverSecurityLogic(
        groupValidationService.validateGroupPublicity
      )
      .serverLogicSuccess(
        group => {case (count, higherNumber) => postController.getPosts(group, count, higherNumber)}
      )

}
