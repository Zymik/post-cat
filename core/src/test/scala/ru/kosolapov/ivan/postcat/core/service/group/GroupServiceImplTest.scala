package ru.kosolapov.ivan.postcat.core.service.group

import cats.Id
import org.scalatest.OptionValues
import ru.kosolapov.ivan.postcat.common.domain.group.{Group, GroupId}
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.domain.user.{User, UserId}
import ru.kosolapov.ivan.postcat.common.dto.CreationStatus
import ru.kosolapov.ivan.postcat.core.Test
import ru.kosolapov.ivan.postcat.core.repository.group.GroupRepository
import ru.kosolapov.ivan.postcat.core.repository.telegram.group.TelegramChannelGroupRepository
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramChannelId

import java.util.UUID

class GroupServiceImplTest extends Test with OptionValues {
  private val groupRepositoryMock = mock[GroupRepository[Id]]
  private val telegramGroupRepository = mock[TelegramChannelGroupRepository[Id]]

  private val groupService = new GroupServiceImpl(
    groupRepositoryMock,
    telegramGroupRepository
  )

  private val name = "name"
  private val telegramUserId = TelegramUserId(0)
  private val userId = UserId(UUID.randomUUID())
  private val user = User(userId, telegramUserId)

  private val groupName = "group"
  private val groupId = GroupId(0)
  private val group = Group(groupId, user, groupName)

  "If group already created" should "return Exist" in {
    groupRepositoryMock.contains _ expects(userId, groupName) returns true

    groupService.createGroup(userId, groupName) shouldBe CreationStatus.Exist
  }

  "If created new group" should "return Created" in {
    groupRepositoryMock.contains _ expects(userId, groupName) returns false
    groupRepositoryMock.createGroup _ expects(userId, groupName) returns CreationStatus.Created

    groupService.createGroup(userId, groupName) shouldBe CreationStatus.Created
  }

  "If group don't exist" should "return None" in {
    groupRepositoryMock.getGroupId _ expects(userId, groupName) returns None

    groupService.getGroup(user, groupName) shouldBe None
  }

  "If group exist" should "return Some" in {
    groupRepositoryMock.getGroupId _ expects(userId, groupName) returns Some(groupId)

    groupService.getGroup(user, groupName).value shouldBe group
  }

  "Set rest api publicity" should "invoke group repository method" in {
    groupRepositoryMock.setRestApiPublicity _ expects (groupId, true)

    groupService.setRestApiPublicity(groupId, publicity = true)
  }

  private val channels = Set(TelegramChannelId("id1"), TelegramChannelId("id2"))
  "Add telegram channels" should "invoke telgeram channel repository method" in {
    telegramGroupRepository.addChannels _ expects(groupId, channels)

    groupService.addTelegramChannels(groupId, channels)
  }
}
