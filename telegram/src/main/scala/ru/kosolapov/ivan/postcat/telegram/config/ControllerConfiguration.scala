package ru.kosolapov.ivan.postcat.telegram.config

import cats.MonadThrow
import org.typelevel.log4cats.Logger
import ru.kosolapov.ivan.postcat.telegram.controller.group.{GroupController, GroupControllerImpl}
import ru.kosolapov.ivan.postcat.telegram.controller.post.{PostController, PostControllerImpl}
import ru.kosolapov.ivan.postcat.telegram.controller.register.{RegisterController, RegisterControllerImpl}

class ControllerConfiguration[F[_] : MonadThrow : Logger]
(
  serviceConfiguration: ServiceConfiguration[F]
) {

  val postController: PostController[F] = new PostControllerImpl[F](
    serviceConfiguration.postService
  )

  val registerController: RegisterController[F] = new RegisterControllerImpl[F](
    serviceConfiguration.registerService
  )

  val groupController: GroupController[F] = new GroupControllerImpl[F](
    serviceConfiguration.groupService,
    serviceConfiguration.rightsService
  )

}
