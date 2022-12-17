package ru.kosolapov.ivan.postcat.common

import cats.{Functor, MonadThrow}
import cats.data.EitherT
import cats.effect.Concurrent
import cats.implicits.catsSyntaxApplicativeErrorId
import io.circe.{Decoder, HCursor}
import cats.syntax.all._
import org.http4s.Status.{ClientError, Successful}
import org.http4s.{Request, Response}
import org.http4s.circe.jsonOf
import org.typelevel.log4cats.Logger
import ru.kosolapov.ivan.postcat.common.dto.ApiError
import io.circe.generic.auto._
import org.http4s.client.Client

import java.io.IOException

package object implicits {

  implicit class ResponseDecode[F[_] : MonadThrow : Concurrent](val response: Response[F]) {

    /**
     * If response success try to decode result. If client error try to decode [[ApiError]].
     * Else throws [[IOException]]
     *
     * @param request request of response. Needed to log error.
     * @tparam A type of result
     */
    def decode[A: Decoder](request: Request[F]): EitherT[F, ApiError, A] = {
      response.status.responseClass match {
        case Successful => EitherT.right(
          jsonOf[F, A]
            .decode(response, strict = false)
            .rethrowT
        )

        case ClientError => EitherT.left(
          jsonOf[F, ApiError]
            .decode(response, strict = false)
            .rethrowT
        )

        case _ =>
          EitherT.right(
            new IOException(s"Unexpected http status.\nRequest:\n${request.uri}")
              .raiseError
          )
      }

    }

  }

  implicit class ClientWithApiError[F[_] : MonadThrow : Concurrent](val client: Client[F]) {

    /**
     * Run request on client and then execute [[ResponseDecode.decode]]
     */
    def runWithDecode[A: Decoder](request: Request[F]): EitherT[F, ApiError, A] =
      for {
        response <- EitherT.right(client.run(request).use(_.pure))
        decoded <- response.decode[A](request)
      } yield decoded
  }

  /**
   * Unit decoder that ignore input
   */
  implicit val UnitDecoder: Decoder[Unit] = (_: HCursor) => Right(())


  implicit class EitherApiError[F[_] : Functor](val eitherT: EitherT[F, ApiError, String]) {

    /**
     * Handle ApiError by getting message
     */
    def errorToString: F[String] =
      eitherT
        .valueOr(_.message)
  }

  implicit class MonadThrowLogger[F[_] : MonadThrow : Logger, A](val monad: F[A]) {

    /**
     * Log throwable with message and map it with handler
     */
    def logErrorHandleWith(message: String, handler: Throwable => F[A]): F[A] =
      monad
        .handleErrorWith(
          throwable =>
            for {
              _ <- Logger[F].error(throwable)(message)
              result <- handler(throwable)
            } yield result
        )

    /**
     * Log throwable with message and map it then with handler and put result in F
     */
    def logErrorHandle(message: String, handler: Throwable => A): F[A] =
      monad
        .handleErrorWith(
          throwable =>
            for {
              _ <- Logger[F].error(throwable)(message)
            } yield handler(throwable)
        )

  }
}
