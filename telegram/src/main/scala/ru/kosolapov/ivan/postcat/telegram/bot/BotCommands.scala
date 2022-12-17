package ru.kosolapov.ivan.postcat.telegram.bot
import cats.{Applicative, Functor, Monad}
import cats.data.{Kleisli, NonEmptyList}
import cats.parse.Parser0
import cats.syntax.all._
import ru.kosolapov.ivan.postcat.common.command.dsl._
import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramUserId}
import ru.kosolapov.ivan.postcat.telegram.controller.group.GroupController
import ru.kosolapov.ivan.postcat.telegram.controller.register.RegisterController
import telegramium.bots.Message

/**
 * Configuration of bot commands with [[ru.kosolapov.ivan.postcat.common.command.dsl]]
 */
class BotCommands[F[_] : Monad]
(
  groupController: GroupController[F],
  registerController: RegisterController[F]
) {

  type BotCommands[G[_]] = Commands[G, Message, String]

  // Methods wrappers
  private def register(telegramUserId: TelegramUserId) =
    (_: Unit) => registerController.register(telegramUserId)

  private def addChannels(telegramUserId: TelegramUserId) =
    (groupController.addChannels(telegramUserId) _).tupled

  private def postToGroup = (groupController.postToGroup _).tupled

  private val publicToRestApi =
    (groupController.setRestApiPublicity _)(true)

  private val closeToRestApi =
    (groupController.setRestApiPublicity _)(false)

  // Context resolvers
  private def getUser(message: Message): Option[TelegramUserId] =
    for {
      userId <- message.from.map(u => TelegramUserId(u.id))
    } yield userId

  private implicit val userIdContext: Kleisli[Option, Message, TelegramUserId] = Kleisli(getUser)

  private val replyContext = Kleisli((m: Message) => m.replyToMessage.flatMap(_.text))


  private implicit val channelsParser: Parser0[NonEmptyList[TelegramChannelId]] =
    arg[NonEmptyList[String]]
      .map(_.map(TelegramChannelId))


  // Commands description
  private val commands: BotCommands[F] = {
      "/start" / "Register in post service" / arg[Unit] ~> register |
      "/create_group" / "Create group with name" / arg[String] ~> groupController.createGroup |
      "/add_channels" / "Add channels to group" / arg[String] ~ arg[NonEmptyList[TelegramChannelId]] ~> addChannels |
      "/post" / "Post message to group" / arg[String] ~| (userIdContext product replyContext) ~> postToGroup |
      "/public_to_rest_api" / "Make available to get posts from rest api" / arg[String] ~> publicToRestApi |
      "/close_to_rest_api" / "Make unavailable to get posts from rest api" / arg[String] ~> closeToRestApi
  }

  def execute(message: Message) : Option[CommandExecution[F, String]] =
    message.text
      .flatMap(commands.execute(message))

  def description: List[CommandDescription] = commands.description

}
