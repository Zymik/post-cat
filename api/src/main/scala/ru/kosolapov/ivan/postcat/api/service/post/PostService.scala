package ru.kosolapov.ivan.postcat.api.service.post

import ru.kosolapov.ivan.postcat.common.domain.group.GroupId
import ru.kosolapov.ivan.postcat.common.domain.post.Post

trait PostService[F[_]] {

  /**
   * Get count posts of group that have id lower than higherId.
   * @return list of posts. List can have size less than count param,
   *         if total count of posts with correct id are less than it
   */
  def getPosts(groupId: GroupId, count: Int, higherNumber: Long): F[List[Post]]

  /**
   * Get count posts of group with highest ids
   *
   * @return list of posts. List can size less than count param,
   *         if total count of posts are less than it
   */
  def getLastPosts(groupId: GroupId, count: Int): F[List[Post]]
}
