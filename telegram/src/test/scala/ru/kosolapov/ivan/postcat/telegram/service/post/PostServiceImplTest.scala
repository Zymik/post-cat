package ru.kosolapov.ivan.postcat.telegram.service.post

import cats.Id
import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramUserId}
import ru.kosolapov.ivan.postcat.common.dto.PostStatus
import ru.kosolapov.ivan.postcat.telegram.Test
import ru.kosolapov.ivan.postcat.telegram.client.ChatClient
import ru.kosolapov.ivan.postcat.telegram.service.rights.RightsService

class PostServiceImplTest extends Test {

  private val chatClient = mock[ChatClient[Id]]
  private val rightsService = mock[RightsService[Id]]

  private val userId = TelegramUserId(0)
  private val channelId: TelegramChannelId = TelegramChannelId("@chat")

  private val postService = new PostServiceImpl(
    rightsService,
    chatClient
  )

  private val text = "text"

  "if bot and user have rights" should "return success" in {
    rightsService.canBotPost _ expects channelId returns true
    rightsService.canPost _ expects(channelId, userId) returns true
    chatClient.sendMessage _ expects(channelId, text)

    postService.postToChannel(userId, channelId, text) shouldBe PostStatus.PostSuccess
  }

  "if bot have no rights" should "return BotNoRightsToPost" in {
    rightsService.canBotPost _ expects channelId returns false

    postService.postToChannel(userId, channelId, text) shouldBe PostStatus.PostFailure(PostStatus.BotNoRightsToPost)
  }

  "if user have no rights" should "return UserNoRightsToPost" in {
    rightsService.canBotPost _ expects channelId returns true
    rightsService.canPost _ expects(channelId, userId) returns false

    postService.postToChannel(userId, channelId, text) shouldBe PostStatus.PostFailure(PostStatus.UserNoRightsToPost)
  }

}
