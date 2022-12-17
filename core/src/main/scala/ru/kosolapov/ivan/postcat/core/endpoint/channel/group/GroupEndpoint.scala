package ru.kosolapov.ivan.postcat.core.endpoint.channel.group

import io.circe.generic.auto._
import ru.kosolapov.ivan.postcat.common.domain.post.Post
import ru.kosolapov.ivan.postcat.common.dto.{CreationStatus, PostStatus}
import ru.kosolapov.ivan.postcat.common.endpoint.Headers
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramChannelId
import ru.kosolapov.ivan.postcat.core.controller.group.GroupController
import ru.kosolapov.ivan.postcat.core.endpoint.security.channel.group.GroupSecurityEndpoint
import ru.kosolapov.ivan.postcat.core.endpoint.security.user.UserSecurityEndpoint
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint


class GroupEndpoint[F[_]]
(
  securityEndpoint: UserSecurityEndpoint[_, F],
  groupSecurityEndpoint: GroupSecurityEndpoint[_, F],
  groupController: GroupController[F]
) {

  private val createChannelGroupEndpoint: ServerEndpoint[Any, F] = {
    securityEndpoint
      .securityEndpoint
      .description("Create channel group with name for given user")
      .post
      .in("group" / "create")
      .in(header[String](Headers.groupName).validate(Validator.nonEmptyString))
      .out(jsonBody[CreationStatus])
      .serverLogicSuccess(user => groupController.createGroup(user.userId, _))
  }

  private val addChannelsToGroupEndpoint: ServerEndpoint[Any, F] =
    groupSecurityEndpoint
      .endpoint
      .description("Add channels to group with given name and owner")
      .post
      .in("group" / "add")
      .in(jsonBody[Set[TelegramChannelId]])
      .out(emptyOutput)
      .serverLogicSuccess(
        group => channels => groupController.addTelegramChannels(group, channels)
      )

  private val postToChannelsOfGroup: ServerEndpoint[Any, F] =
    groupSecurityEndpoint
      .endpoint
      .description("Send post to all channels of group")
      .post
      .in("group" / "post")
      .in(jsonBody[Post])
      .out(jsonBody[List[(String, PostStatus)]])
      .serverLogicSuccess(
        group => post => groupController.post(group, post)
      )

  private val setRestApiPublicity: ServerEndpoint[Any, F] =
    groupSecurityEndpoint
      .endpoint
      .description("Set group visibility to rest api to passed flag value")
      .in("group" / "restApiPublicity")
      .in(query[Boolean]("publicity"))
      .out(emptyOutput)
      .serverLogicSuccess(
        group => publicity => groupController.setRestApiPublicity(group, publicity)
      )

  val endpoints: List[ServerEndpoint[Fs2Streams[F], F]]  = List(
    createChannelGroupEndpoint,
    addChannelsToGroupEndpoint,
    postToChannelsOfGroup,
    setRestApiPublicity
  )
}
