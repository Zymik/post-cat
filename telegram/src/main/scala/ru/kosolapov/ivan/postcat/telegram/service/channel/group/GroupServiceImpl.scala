package ru.kosolapov.ivan.postcat.telegram.service.channel.group

import cats.data.EitherT
import cats.effect.kernel.{Concurrent, MonadCancelThrow}
import io.circe.Encoder.AsObject.importedAsObjectEncoder
import io.circe.generic.auto._
import org.http4s.Request
import org.http4s.circe.jsonEncoderOf
import org.http4s.client.Client
import ru.kosolapov.ivan.postcat.common.domain.post.Post
import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramUserId}
import ru.kosolapov.ivan.postcat.common.dto.{ApiError, CreationStatus, PostStatus}
import ru.kosolapov.ivan.postcat.common.implicits.{ClientWithApiError, UnitDecoder}
import ru.kosolapov.ivan.postcat.telegram.implicits.SecurityRequestExt

class GroupServiceImpl[F[_] : MonadCancelThrow : Concurrent]
(
  httpClient: Client[F],
  baseRequest: Request[F]
) extends GroupService[F] {

  private val createRequest = baseRequest.withUri(
    baseRequest.uri / "create"
  )


  override def createGroup(ownerId: TelegramUserId, groupName: String): EitherT[F, ApiError, CreationStatus] = {
    val request = createRequest
      .putUser(ownerId)
      .putGroup(groupName)

    httpClient
      .runWithDecode(request)
  }

  private val addRequest = baseRequest.withUri(
    baseRequest.uri / "add"
  )

  override def addChannelsToGroup(ownerId: TelegramUserId, groupName: String, channels: Set[TelegramChannelId]): EitherT[F, ApiError, Unit] = {
    val request = addRequest
      .putUser(ownerId)
      .putGroup(groupName)
      .withBodyStream(
        jsonEncoderOf[F, Set[TelegramChannelId]].toEntity(channels).body
      )

    httpClient
      .runWithDecode(request)
  }

  private val postRequest = baseRequest.withUri(
    baseRequest.uri / "post"
  )

  override def postToGroup(ownerId: TelegramUserId, groupName: String, text: String): EitherT[F, ApiError, List[(String, PostStatus)]] = {
    val request = postRequest
      .putUser(ownerId)
      .putGroup(groupName)
      .withBodyStream(
        jsonEncoderOf[F, Post].toEntity(Post(text)).body
      )
    httpClient
      .runWithDecode(request)
  }

  private val publicityRequest = baseRequest.withUri(
    baseRequest.uri / "restApiPublicity"
  )

  override def setRestApiPublicity(ownerId: TelegramUserId, groupName: String, publicity: Boolean): EitherT[F, ApiError, Unit] = {
    val request = publicityRequest
      .putUser(ownerId)
      .putGroup(groupName)
      .withUri(
        publicityRequest.uri.withQueryParam("publicity", publicity)
      )

    httpClient
      .runWithDecode(request)
  }
}
