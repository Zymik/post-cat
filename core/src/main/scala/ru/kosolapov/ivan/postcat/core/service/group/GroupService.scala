package ru.kosolapov.ivan.postcat.core.service.group

import ru.kosolapov.ivan.postcat.common.domain.group.{Group, GroupId}
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramChannelId
import ru.kosolapov.ivan.postcat.common.domain.user.{User, UserId}
import ru.kosolapov.ivan.postcat.common.dto.CreationStatus

trait GroupService[F[_]] {

  /**
   * Create group specified by owner and groupName
   * @return [[CreationStatus.Created]] if account was created and
   * [[CreationStatus.Exist]] if account exist
   */
  def createGroup(ownerId: UserId, groupName: String): F[CreationStatus]

  /**
   * Add telegram channels to group, throw exception if group is not exist
   */
  def addTelegramChannels(groupId: GroupId, channels: Set[TelegramChannelId]): F[Unit]

  /**
   * Get group specified by owner and groupName
   * @return [[Some]] with [[Group]] if group exist, [[None]] if not
   */
  def getGroup(ownerId: User, groupName: String): F[Option[Group]]

  /**
   * Set group publicity to rest api
   * @param publicity publicity, true to make public, false to make private
   */
  def setRestApiPublicity(groupId: GroupId, publicity: Boolean): F[Unit]
}
