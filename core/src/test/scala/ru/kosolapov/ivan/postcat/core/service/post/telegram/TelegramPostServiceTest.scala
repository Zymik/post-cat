package ru.kosolapov.ivan.postcat.core.service.post.telegram

import cats.Id
import ru.kosolapov.ivan.postcat.common.domain.group._
import ru.kosolapov.ivan.postcat.common.domain.post.Post
import ru.kosolapov.ivan.postcat.common.domain.telegram._
import ru.kosolapov.ivan.postcat.common.domain.user._
import ru.kosolapov.ivan.postcat.common.dto.PostStatus
import ru.kosolapov.ivan.postcat.core.Test
import ru.kosolapov.ivan.postcat.core.client.telegram.TelegramClient
import ru.kosolapov.ivan.postcat.core.repository.telegram.group.TelegramChannelGroupRepository
import ru.kosolapov.ivan.postcat.core.service.post.telegram.TelegramPostService

import java.util.UUID

class TelegramPostServiceTest extends Test {

  private val telegramClientMock = mock[TelegramClient[Id]]
  private val telegramChannelGroupRepository = mock[TelegramChannelGroupRepository[Id]]

  private val postService = new TelegramPostService[Id](
    telegramChannelGroupRepository,
    telegramClientMock
  )

  private val userId = UserId(UUID.randomUUID())
  private val telegramUserId = TelegramUserId(0)
  private val owner = User(userId, telegramUserId)

  private val groupId = GroupId(0)
  private val group = Group(groupId, owner, "name")

  private val post = Post("text")

  private val firstName = "@channel1"
  private val secondName = "@channel2"
  private val channels = List(firstName, secondName)
  private val telegramChannels = channels.map(TelegramChannelId)

  private val telegramPost = TelegramPost(telegramUserId, post, telegramChannels)
  private val statuses = List(PostStatus.PostSuccess, PostStatus.PostFailure(PostStatus.Unexpected))

  private val postResult = telegramChannels.zip(statuses)

  private val operationResult = channels.zip(statuses)

  it should s"return $operationResult" in {
    telegramChannelGroupRepository.getTelegramChannelsByGroup _ expects groupId returns telegramChannels
    telegramClientMock.sendPost _ expects telegramPost returns postResult

    postService.post(group, post) shouldEqual operationResult

  }


}
