package ru.kosolapov.ivan.postcat.core.service.post.database

import cats.effect.IO
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.noop.NoOpLogger
import ru.kosolapov.ivan.postcat.common.domain.group.{Group, GroupId}
import ru.kosolapov.ivan.postcat.common.domain.post.Post
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.domain.user.{User, UserId}
import ru.kosolapov.ivan.postcat.common.dto.PostStatus
import ru.kosolapov.ivan.postcat.core.IOTest
import ru.kosolapov.ivan.postcat.core.repository.post.PostRepository
import ru.kosolapov.ivan.postcat.core.service.post.database.DatabasePostService

import java.io.IOException
import java.util.UUID

class DatabasePostServiceTest extends IOTest {

  private implicit def logger: Logger[IO] = NoOpLogger[IO]

  private val postRepositoryMock: PostRepository[IO] = mock[PostRepository[IO]]

  private val databasePostService = new DatabasePostService(
    postRepositoryMock
  )

  private val groupId = GroupId(0)
  private val owner = User(UserId(UUID.randomUUID()), TelegramUserId(0))
  private val group = Group(groupId, owner, "name")
  private val post = Post("text")

  private val name = "Posts storage"
  private val success = List((name, PostStatus.PostSuccess))
  private val failure = List((name, PostStatus.PostFailure(PostStatus.Unexpected)))

  "DatabasePostService test" - {

    "return success" in {
      postRepositoryMock.addPost _ expects  (groupId, post) returns IO.unit

      databasePostService.post(group, post).asserting(_ shouldBe success)
    }

    "return failure on exception" in {
      postRepositoryMock.addPost _ expects (groupId, post) returns IO.raiseError(new IOException())

      databasePostService.post(group, post).asserting(_ shouldBe failure)
    }

  }


}
