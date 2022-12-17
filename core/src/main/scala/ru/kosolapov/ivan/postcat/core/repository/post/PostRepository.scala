package ru.kosolapov.ivan.postcat.core.repository.post

import ru.kosolapov.ivan.postcat.common.domain.group.GroupId
import ru.kosolapov.ivan.postcat.common.domain.post.Post

trait PostRepository[F[_]] {

  /**
   * Add post to database
   */
  def addPost(groupId: GroupId, post: Post) : F[Unit]

}
