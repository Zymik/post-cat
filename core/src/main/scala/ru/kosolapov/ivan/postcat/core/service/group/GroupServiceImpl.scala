package ru.kosolapov.ivan.postcat.core.service.group

import cats.Monad
import cats.syntax.all._
import ru.kosolapov.ivan.postcat.common.domain.group
import ru.kosolapov.ivan.postcat.common.domain.group.{Group, GroupId}
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramChannelId
import ru.kosolapov.ivan.postcat.common.domain.user.{User, UserId}
import ru.kosolapov.ivan.postcat.common.dto.CreationStatus
import ru.kosolapov.ivan.postcat.core.repository.group.GroupRepository
import ru.kosolapov.ivan.postcat.core.repository.telegram.group.TelegramChannelGroupRepository

class GroupServiceImpl[F[_] : Monad]
(
  groupRepository: GroupRepository[F],
  telegramChannelGroupRepository: TelegramChannelGroupRepository[F]
) extends GroupService[F] {

  override def createGroup(ownerId: UserId, groupName: String): F[CreationStatus] = {
    for {
      contains <- groupRepository.contains(ownerId, groupName)
      status <- if (contains)
        CreationStatus.Exist.pure
      else
        groupRepository.createGroup(ownerId, groupName)
    } yield status
  }

  override def addTelegramChannels(groupId: GroupId, channels: Set[TelegramChannelId]): F[Unit] =
    telegramChannelGroupRepository.addChannels(groupId, channels)

  override def getGroup(owner: User, groupName: String): F[Option[Group]] =
    groupRepository.getGroupId(owner.userId, groupName)
      .map(
        _.map(group.Group(_, owner, groupName))
      )

  override def setRestApiPublicity(groupId: GroupId, publicity: Boolean): F[Unit] =
    groupRepository.setRestApiPublicity(groupId, publicity)
}
