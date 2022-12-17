package ru.kosolapov.ivan.postcat.common.dto

sealed trait PostStatus

object PostStatus {
  case object PostSuccess extends PostStatus

  case class PostFailure(reason: FailureReason) extends PostStatus

  sealed trait FailureReason

  case object BotNoRightsToPost extends FailureReason

  case object UserNoRightsToPost extends FailureReason

  case object Unexpected extends FailureReason
}
