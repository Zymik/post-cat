package ru.kosolapov.ivan.postcat.core.controller.group

import ru.kosolapov.ivan.postcat.common.domain.group.Group
import ru.kosolapov.ivan.postcat.common.domain.post.Post
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramChannelId
import ru.kosolapov.ivan.postcat.common.domain.user.UserId
import ru.kosolapov.ivan.postcat.common.dto.{CreationStatus, PostStatus}

trait GroupController[F[_]] {

  /**
   * Create group specified by owner and groupName
   *
   * @return [[CreationStatus.Created]] if account was created and
   *         [[CreationStatus.Exist]] if account exist
   */
  def createGroup(owner: UserId, name: String): F[CreationStatus]

  /**
   * Add telegram channels to group, throw exception if group is not exist
   */
  def addTelegramChannels(group: Group, channels: Set[TelegramChannelId]): F[Unit]

  /**
   * Send post to some destination and return sending result in list
   *
   * @return pairs of [[String]] and [[PostStatus]], [[PostStatus.PostSuccess]] if post succeed
   *         and [[PostStatus.PostFailure]] if post failed
   */
  def post(group: Group, post: Post): F[List[(String, PostStatus)]]

  /**
   * Set group publicity to rest api
   *
   * @param publicity publicity, true to make public, false to make private
   */
  def setRestApiPublicity(group: Group, publicity: Boolean): F[Unit]

}
