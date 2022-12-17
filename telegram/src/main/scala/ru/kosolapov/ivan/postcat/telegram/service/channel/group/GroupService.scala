package ru.kosolapov.ivan.postcat.telegram.service.channel.group

import cats.data.EitherT
import ru.kosolapov.ivan.postcat.common.dto.{ApiError, CreationStatus, PostStatus}
import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramUserId}

trait GroupService[F[_]] {

  /**
   * Creates group specified by owner and groupName. If that group already exists, do nothing.
   * @return [[CreationStatus]] - result of creating group
   */
  def createGroup(ownerId: TelegramUserId, groupName: String) : EitherT[F, ApiError, CreationStatus]

  /**
   * Add telegram channels to group specified by owner and groupName
   * @return
   */
  def addChannelsToGroup(ownerId: TelegramUserId, groupName: String, channels: Set[TelegramChannelId]) : EitherT[F, ApiError, Unit]

  /** Post text to group specified by owner and groupName
   * @return [[List]] of pairs of destination names and result of posting to them
   */
  def postToGroup(ownerId: TelegramUserId, groupName: String, text: String) : EitherT[F, ApiError, List[(String, PostStatus)]]

  /**
   * Make posts of group specified by ownerId and groupName public/private to Rest API
   */
  def setRestApiPublicity(ownerId: TelegramUserId, groupName: String, publicity: Boolean) : EitherT[F, ApiError, Unit]

}
