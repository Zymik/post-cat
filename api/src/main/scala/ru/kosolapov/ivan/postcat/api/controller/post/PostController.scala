package ru.kosolapov.ivan.postcat.api.controller.post

import ru.kosolapov.ivan.postcat.common.domain.group.GroupId
import ru.kosolapov.ivan.postcat.common.domain.post.Post

trait PostController[F[_]] {

  /**
   * Get last count ports of group, that indexes are less than higherNumber.
   * if HigherNumber is None take last count posts
   * @return list of post. List size can be less than count,
   *         if count of correct post are less than count
   */
  def getPosts(groupId: GroupId, count: Int, higherNumber: Option[Long]): F[List[Post]]

}
