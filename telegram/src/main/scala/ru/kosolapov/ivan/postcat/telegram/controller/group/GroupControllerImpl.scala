package ru.kosolapov.ivan.postcat.telegram.controller.group

import cats.MonadThrow
import cats.data.{EitherT, NonEmptyList}
import cats.syntax.all._
import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramUserId}
import ru.kosolapov.ivan.postcat.common.dto.CreationStatus.{Created, Exist}
import ru.kosolapov.ivan.postcat.common.dto.PostStatus
import ru.kosolapov.ivan.postcat.common.dto.PostStatus._
import ru.kosolapov.ivan.postcat.common.implicits.EitherApiError
import ru.kosolapov.ivan.postcat.telegram.service.channel.group.GroupService
import ru.kosolapov.ivan.postcat.telegram.service.rights.RightsService

class GroupControllerImpl[F[_] : MonadThrow]
(
  groupService: GroupService[F],
  rightsService: RightsService[F]
) extends GroupController[F] {

  override def createGroup(userId: TelegramUserId)(groupName: String): F[String] =
    groupService
      .createGroup(userId, groupName)
      .map {
        case Exist => "Group are already created"
        case Created => "Group were created!"
      }
      .errorToString

  override def addChannels(userId: TelegramUserId)(groupName: String, channels: NonEmptyList[TelegramChannelId]): F[String] = {
    val channelsValidated = EitherT.right(channels
      .toList
      .map(validateChannel(userId))
      .sequenceFilter
      .map(_.toSet)
    )

    channelsValidated.flatMap(
      set => groupService.addChannelsToGroup(userId, groupName, set) >> EitherT.right(set.pure)
    ).map(set => getAddChannelsAnswer(channels, set))
      .errorToString
  }

  private def getAddChannelsAnswer(channels: NonEmptyList[TelegramChannelId], set: Set[TelegramChannelId]) = {
    val unvalidated = channels.filterNot(set.contains)

    if (unvalidated.isEmpty)
      s"All channels were added"
    else
      s"Can't add those, you or bot have no rights to post: ${unvalidated.map(_.id).mkString(" ")}"

  }

  override def postToGroup(userId: TelegramUserId, text: String)(groupName: String): F[String] =
    groupService
      .postToGroup(userId, groupName, text)
      .map(postResultToString)
      .errorToString


  override def setRestApiPublicity(publicity: Boolean)(userId: TelegramUserId)(groupName: String): F[String] = {
    groupService
      .setRestApiPublicity(userId, groupName, publicity)
      .map(_ => "Successfully set group api publicity")
      .errorToString
  }

  private def validateChannel(userId: TelegramUserId)(channelId: TelegramChannelId) = {

    val validated = for {
      userCanPost <- rightsService.canPost(channelId, userId)
      botCanPost <- rightsService.canBotPost(channelId)
    } yield {
      if (userCanPost && botCanPost)
        Some(channelId)
      else
        None
    }
    validated.handleError(_ => None)
  }


  private def postResultToString(posts: List[(String, PostStatus)]): String = {
    val errors = posts
      .filterNot(_._2 == PostSuccess)
      .map {
        case (name, PostFailure(reason)) => s"$name: ${failReasonToString(reason)}"
      }
      .mkString("\n")

    s"Fails while posting:\n$errors "
  }

  private def failReasonToString(reason: FailureReason): String =
    reason match {
      case BotNoRightsToPost => "Bot have no rights to post"
      case UserNoRightsToPost => "User have nor right to post"
      case Unexpected => "Unexpected failure"
    }


}
