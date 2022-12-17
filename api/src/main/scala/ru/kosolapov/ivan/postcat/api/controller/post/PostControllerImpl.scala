package ru.kosolapov.ivan.postcat.api.controller.post

import ru.kosolapov.ivan.postcat.common.domain.group.GroupId
import ru.kosolapov.ivan.postcat.common.domain.post.Post
import ru.kosolapov.ivan.postcat.api.service.post.PostService

class PostControllerImpl[F[_]]
(
  postService: PostService[F]
) extends PostController[F] {

  override def getPosts(groupId: GroupId, count: Int, higherNumber: Option[Long]): F[List[Post]] = {
    higherNumber match {
      case Some(number) => postService.getPosts(groupId, count, number)
      case None => postService.getLastPosts(groupId, count)
    }
  }
}
