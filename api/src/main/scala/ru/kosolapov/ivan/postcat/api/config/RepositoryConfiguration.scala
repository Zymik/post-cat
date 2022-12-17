package ru.kosolapov.ivan.postcat.api.config

import cats.effect.Async
import doobie.util.transactor.Transactor
import ru.kosolapov.ivan.postcat.api.repository.group.{DatabaseGroupRepository, GroupRepository}
import ru.kosolapov.ivan.postcat.api.repository.post.{DatabasePostRepository, PostRepository}

class RepositoryConfiguration[F[_] : Async]
(
  xa: Transactor[F]
) {

  val postRepository: PostRepository[F] = new DatabasePostRepository(xa)

  val groupRepository: GroupRepository[F] = new DatabaseGroupRepository(xa)
}
