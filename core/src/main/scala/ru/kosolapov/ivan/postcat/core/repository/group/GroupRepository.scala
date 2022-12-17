package ru.kosolapov.ivan.postcat.core.repository.group

import ru.kosolapov.ivan.postcat.common.dto.CreationStatus
import ru.kosolapov.ivan.postcat.common.domain.group.GroupId
import ru.kosolapov.ivan.postcat.common.domain.user.UserId

trait GroupRepository[F[_]] {

  /**
   * Check that group specified by owner and groupName contains in database
   */
  def contains(ownerId: UserId, groupName: String): F[Boolean]

  /**
   * Create new group specified by ownerId and groupName
   * @return [[CreationStatus]] - result of creating group
   */
  def createGroup(ownerId: UserId, groupName: String): F[CreationStatus]

  /**
   * Get group specified by ownerId and groupName
   * @return [[Some]] of with [[GroupId]] if group exist, [[None]] otherwise
   */
  def getGroupId(ownerId: UserId, groupName: String): F[Option[GroupId]]

  /**
   * Set publicity value of group in database
   */
  def setRestApiPublicity(groupId: GroupId, publicity: Boolean) : F[Unit]

}
