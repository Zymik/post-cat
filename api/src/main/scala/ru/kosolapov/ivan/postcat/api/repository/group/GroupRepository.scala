package ru.kosolapov.ivan.postcat.api.repository.group

import ru.kosolapov.ivan.postcat.common.domain.group.GroupId

trait GroupRepository[F[_]] {

  /**
   * Check that group is public to rest api
   * @return true if groupId is public to rest api, else false
   */
  def isPublicGroupId(groupId: GroupId) : F[Boolean]

}
