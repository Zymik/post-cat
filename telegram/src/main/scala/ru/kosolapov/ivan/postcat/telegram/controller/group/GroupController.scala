package ru.kosolapov.ivan.postcat.telegram.controller.group

import cats.data.NonEmptyList
import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramUserId}

/**
 * All methods returns [[Some]] with message to answer or None if no message to answer
 */
trait GroupController[F[_]] {

  /**
   * Create group specified by user and groupName
   */
  def createGroup(userId: TelegramUserId)(groupName: String): F[String]

  /**
   * Add telegram channels to group specified by user and groupName
   */
  def addChannels(userId: TelegramUserId)(groupName: String, channels: NonEmptyList[TelegramChannelId]) : F[String]

  /**
   * Post text to group specified by user and groupName
   */
  def postToGroup(userId: TelegramUserId, text: String)(groupName: String): F[String]

  /**
   * Set publicity to group specified by user and groupName
   */
  def setRestApiPublicity(publicity: Boolean)(userId: TelegramUserId)(groupName: String): F[String]
}
