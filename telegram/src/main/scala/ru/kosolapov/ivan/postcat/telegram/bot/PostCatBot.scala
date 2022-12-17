package ru.kosolapov.ivan.postcat.telegram.bot

import cats.Parallel
import cats.effect.Async
import cats.syntax.all._
import org.typelevel.log4cats.Logger
import ru.kosolapov.ivan.postcat.common.command.dsl.CommandExecution
import ru.kosolapov.ivan.postcat.common.implicits.MonadThrowLogger
import ru.kosolapov.ivan.postcat.telegram.client.ChatClient
import telegramium.bots.high._
import telegramium.bots.high.implicits._
import telegramium.bots.{ChatIntId, Message}


class PostCatBot[F[_] : Async : Parallel : Logger]
(
  commands: BotCommands[F],
  chatClient: ChatClient[F]
)(implicit api: Api[F]) extends LongPollBot[F](api) {

  override def onMessage(msg: Message): F[Unit] = {
    val execution = for {
      execution <- commands.execute(msg)
    } yield handleExecution(execution)(msg)

    execution.getOrElse(().pure)
  }

  private def handleExecution(commandExecution: CommandExecution[F, String])(implicit context: Message): F[Unit] = {
    val CommandExecution(name, execution) = commandExecution
    Logger[F].info(s"Matched command $name") >>
      execution
        .logErrorHandle(
          s"Exception while executing command $name",
          _ => "Unexpected error( Retry later"
        )
        .flatMap(
          (text: String) => chatClient
            .reply(context, text)
        ) >> ().pure
  }

  private def reply(message: String)(implicit context: Message): F[Unit] = {
    Methods.sendMessage(ChatIntId(context.chat.id), message, replyToMessageId = Some(context.messageId))
      .exec
      .map(_ => ())
  }

}
