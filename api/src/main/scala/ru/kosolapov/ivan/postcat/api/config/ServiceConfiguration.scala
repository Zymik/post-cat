package ru.kosolapov.ivan.postcat.api.config

import cats.Functor
import ru.kosolapov.ivan.postcat.api.service.group.{GroupValidationService, GroupValidationServiceImpl}
import ru.kosolapov.ivan.postcat.api.service.post.{PostService, PostServiceImpl}

class ServiceConfiguration[F[_] : Functor]
(
  repositoryConfiguration: RepositoryConfiguration[F]
)
{
  val groupValidationService: GroupValidationService[F] = new GroupValidationServiceImpl(
    repositoryConfiguration.groupRepository
  )

  val postService: PostService[F] = new PostServiceImpl(
    repositoryConfiguration.postRepository
  )

}
