package ru.kosolapov.ivan.postcat.core.client.telegram

import cats.MonadThrow
import cats.effect.Async
import cats.syntax.all._
import io.circe.generic.auto._
import org.http4s.Request
import org.http4s.circe._
import org.http4s.client.Client
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.syntax._
import ru.kosolapov.ivan.postcat.common.dto.PostStatus
import ru.kosolapov.ivan.postcat.common.dto.PostStatus.Unexpected
import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramPost}

import java.io.IOException


class TelegramClientImpl[F[_] : Async : MonadThrow : Logger]
(
  httpClient: Client[F],
  postRequest: Request[F]
) extends TelegramClient[F]
{

  override def sendPost(telegramPost: TelegramPost): F[List[(TelegramChannelId, PostStatus)]] =
    sendPostWithErrors(telegramPost)
      .handleErrorWith{
        throwable =>
          for {
            _ <- error"Unexpected exception while post request to telegram channels $throwable"
          } yield
            telegramPost.channelIds.map(c => (c, PostStatus.PostFailure(Unexpected)))
      }


  private def sendPostWithErrors(telegramPost: TelegramPost): F[List[(TelegramChannelId, PostStatus)]] = {
    val request = postRequest.withBodyStream(encode(telegramPost))
    httpClient.run(request)
      .use {
        response =>
          jsonOf[F, List[(TelegramChannelId, PostStatus)]]
            .decode(response, strict = false)
            .value
            .map {
              case Right(result) => result
              case Left(error) => throw new IOException(error.toString)
            }
      }
  }

  private def encode(telegramPost: TelegramPost) = {
    jsonEncoderOf[F, TelegramPost].toEntity(telegramPost).body
  }
}
