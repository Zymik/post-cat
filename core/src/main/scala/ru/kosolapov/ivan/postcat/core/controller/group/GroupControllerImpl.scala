package ru.kosolapov.ivan.postcat.core.controller.group

import ru.kosolapov.ivan.postcat.common.domain.group.Group
import ru.kosolapov.ivan.postcat.common.domain.post.Post
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramChannelId
import ru.kosolapov.ivan.postcat.common.domain.user.UserId
import ru.kosolapov.ivan.postcat.common.dto.{CreationStatus, PostStatus}
import ru.kosolapov.ivan.postcat.core.service.group.GroupService
import ru.kosolapov.ivan.postcat.core.service.post.PostService

class GroupControllerImpl[F[_]]
(
  groupService: GroupService[F],
  postService: PostService[F]
) extends GroupController[F] {

  override def createGroup(ownerId: UserId, name: String): F[CreationStatus] =
    groupService.createGroup(ownerId, name)

  override def addTelegramChannels(group: Group, channels: Set[TelegramChannelId]): F[Unit] =
    groupService.addTelegramChannels(group.groupId, channels)

  override def post(group: Group, post: Post): F[List[(String, PostStatus)]] =
    postService.post(group, post)

  override def setRestApiPublicity(group: Group, publicity: Boolean): F[Unit] =
    groupService.setRestApiPublicity(group.groupId, publicity)


}
