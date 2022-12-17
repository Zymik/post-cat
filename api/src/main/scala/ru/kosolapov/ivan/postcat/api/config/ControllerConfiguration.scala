package ru.kosolapov.ivan.postcat.api.config

import ru.kosolapov.ivan.postcat.api.controller.post.{PostController, PostControllerImpl}

class ControllerConfiguration[F[_]]
(
  serviceConfiguration: ServiceConfiguration[F]
) {
  val postController: PostController[F] = new PostControllerImpl(
    serviceConfiguration.postService
  )
}
