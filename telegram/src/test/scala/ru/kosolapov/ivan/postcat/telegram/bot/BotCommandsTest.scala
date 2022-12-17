package ru.kosolapov.ivan.postcat.telegram.bot

import cats.Id
import cats.data.NonEmptyList
import cats.syntax.all._
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramUserId}
import ru.kosolapov.ivan.postcat.telegram.Test
import ru.kosolapov.ivan.postcat.telegram.controller.group.GroupController
import ru.kosolapov.ivan.postcat.telegram.controller.register.RegisterController
import telegramium.bots.{Chat, Message, User}

class BotCommandsTest extends Test {

  private val groupControllerMock = mock[GroupController[Id]]
  private val registerControllerMock = mock[RegisterController[Id]]

  private val idString = "".pure[Id]

  private val id = 0
  private val user = Some(User(0, isBot = false, "Test"))
  private val chat = Chat(0, "test")

  private val telegramUserId = TelegramUserId(id)

  private def messageWithUser(text: String) = Message(0, 0, chat, from = user, text = Some(text))

  private def messageWithoutUser(text: String) = Message(0, 0, chat, text = Some(text))

  private def messageWithReply(text: String) = Message(
    0,
    0,
    chat,
    from = user,
    text = Some(text),
    replyToMessage = Some(messageWithUser(text))
  )

  private val botCommands = new BotCommands(
    groupControllerMock,
    registerControllerMock
  )


  private val start = "/start"

  start should "return Some with user" in {
    (registerControllerMock.register(_: TelegramUserId)) expects telegramUserId returns idString

    botCommands.execute(messageWithUser(start)) should not be empty
  }

  start should "return None without user" in {
    botCommands.execute(messageWithoutUser(start)) shouldBe empty
  }


  private val createGroup = "/create_group"
  private val createGroupWithArgs = s"$createGroup name"

  createGroupWithArgs should "return Some with user" in {
    (groupControllerMock.createGroup(_: TelegramUserId)(_: String)) expects(telegramUserId, "name") returns idString

    botCommands.execute(messageWithUser(createGroupWithArgs)) should not be empty
  }

  createGroupWithArgs should "return None without user" in {

    botCommands.execute(messageWithoutUser(createGroupWithArgs)) shouldBe empty
  }

  createGroup should "return None without args" in {

    botCommands.execute(messageWithUser(createGroup)) shouldBe empty
  }


  private val addChannels = "/add_channels group"
  private val addChannelsWithArgs = s"$addChannels channel1 channel2"

  addChannelsWithArgs should "return Some with user" in {
    val channels = NonEmptyList.of("channel1", "channel2").map(TelegramChannelId)

    (groupControllerMock
      .addChannels(_: TelegramUserId)(_: String, _: NonEmptyList[TelegramChannelId])) expects
      (telegramUserId, "group", channels) returns idString

    botCommands.execute(messageWithUser(addChannelsWithArgs)) should not be empty
  }

  addChannels should "return None without groups" in {
    botCommands.execute(messageWithoutUser(addChannels)) shouldBe empty
  }


  private val post = "/post"
  private val postWithArgs = s"$post name"

  post should "return Some with reply" in {
    (groupControllerMock
      .postToGroup(_: TelegramUserId, _: String)(_: String)) expects(telegramUserId, postWithArgs, "name") returns idString

    botCommands.execute(messageWithReply(postWithArgs)) should not be empty
  }

  post should "return None without reply" in {

    botCommands.execute(messageWithUser(postWithArgs)) shouldBe empty
  }

  post should "return None without args" in {

    botCommands.execute(messageWithReply(post)) shouldBe empty
  }


  private val publicToApi = "/public_to_rest_api"
  private val publicToApiWithArgs = s"$publicToApi name"

  publicToApiWithArgs should "return Some with user" in {
    (groupControllerMock.setRestApiPublicity(_: Boolean)(_: TelegramUserId)(_: String)) expects(true, telegramUserId, "name") returns idString

    botCommands.execute(messageWithUser(publicToApiWithArgs)) should not be empty
  }

  publicToApiWithArgs should "return None without user" in {

    botCommands.execute(messageWithoutUser(publicToApiWithArgs)) shouldBe empty
  }

  publicToApi should "return None without args" in {

    botCommands.execute(messageWithUser(publicToApi)) shouldBe empty
  }


  private val closeToApi = "/close_to_rest_api"
  private val closeToApiWithArgs = s"$closeToApi name"

  closeToApiWithArgs should "return Some with user" in {
    (groupControllerMock.setRestApiPublicity(_: Boolean)(_: TelegramUserId)(_: String)) expects(false, telegramUserId, "name") returns idString

    botCommands.execute(messageWithUser(closeToApiWithArgs)) should not be empty
  }

  closeToApiWithArgs should "return None without user" in {

    botCommands.execute(messageWithoutUser(closeToApiWithArgs)) shouldBe empty
  }

  closeToApi should "return None without args" in {

    botCommands.execute(messageWithUser(closeToApi)) shouldBe empty
  }


}
