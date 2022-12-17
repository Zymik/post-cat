package ru.kosolapov.ivan.postcat.api.service.post

import ru.kosolapov.ivan.postcat.common.domain.group.GroupId
import ru.kosolapov.ivan.postcat.common.domain.post.Post
import ru.kosolapov.ivan.postcat.api.repository.post.PostRepository

class PostServiceImpl[F[_]]
(
  postRepository: PostRepository[F]
) extends PostService[F] {

  override def getPosts(groupId: GroupId, count: Int, higherNumber: Long): F[List[Post]] =
   postRepository.getPosts(groupId, count, higherNumber)

  override def getLastPosts(groupId: GroupId, count: Int): F[List[Post]] =
    postRepository.getLastPosts(groupId, count)
}
