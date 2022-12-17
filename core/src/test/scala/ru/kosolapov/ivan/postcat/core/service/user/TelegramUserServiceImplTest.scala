package ru.kosolapov.ivan.postcat.core.service.user

import cats.Id
import org.scalatest.OptionValues
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.domain.user.{User, UserId}
import ru.kosolapov.ivan.postcat.common.dto.CreationStatus
import ru.kosolapov.ivan.postcat.core.Test
import ru.kosolapov.ivan.postcat.core.repository.telegram.user.TelegramUserRepository
import ru.kosolapov.ivan.postcat.core.service.telegram.user.TelegramUserServiceIml

import java.util.UUID

class TelegramUserServiceImplTest extends Test with OptionValues {

  private val telegramUserRepository = mock[TelegramUserRepository[Id]]
  private val telegramUserId = TelegramUserId(0)

  private val telegramUserService = new TelegramUserServiceIml(
    telegramUserRepository
  )

  "If already registered" should "return Exist" in {
    telegramUserRepository.contains _ expects telegramUserId returns true

    telegramUserService.registerUser(telegramUserId) shouldBe CreationStatus.Exist
  }

  "If registration performed" should "return Created" in {
    telegramUserRepository.contains _ expects telegramUserId returns false
    telegramUserRepository.insert _ expects telegramUserId returns CreationStatus.Created

    telegramUserService.registerUser(telegramUserId) shouldBe CreationStatus.Created
  }

  "If player don't registered" should "return None" in {
    telegramUserRepository.getUserId _ expects telegramUserId returns None

    telegramUserService.getUser(telegramUserId) shouldBe None
  }

  private val userId = UserId(UUID.randomUUID())

  "if player don't registered" should "return Some" in {
    telegramUserRepository.getUserId _ expects telegramUserId returns Some(userId)

    telegramUserService.getUser(telegramUserId).value shouldBe User(userId, telegramUserId)

  }
}
