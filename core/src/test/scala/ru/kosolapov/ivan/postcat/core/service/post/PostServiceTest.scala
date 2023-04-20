package ru.kosolapov.ivan.postcat.core.service.post

import cats.effect.IO
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.noop.NoOpLogger
import ru.kosolapov.ivan.postcat.common.domain.group.{Group, GroupId}
import ru.kosolapov.ivan.postcat.common.domain.post.Post
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.domain.user.{User, UserId}
import ru.kosolapov.ivan.postcat.common.dto.PostStatus
import ru.kosolapov.ivan.postcat.core.IOTest

import java.io.IOException
import java.util.UUID

class PostServiceTest extends IOTest {

  private implicit def logger: Logger[IO] = NoOpLogger[IO]

  private val groupId = GroupId(0)
  private val owner = User(UserId(UUID.randomUUID()), TelegramUserId(0))
  private val group = Group(groupId, owner, "name")
  private val post = Post("text")

  private val firstName = "name1"
  private val secondName = "name2"

  private val firstResult = List((firstName, PostStatus.PostSuccess))
  private val secondResult = List((secondName, PostStatus.PostFailure(PostStatus.Unexpected)))

  private val combinedResult = List(firstResult, secondResult).flatten

  private val firstServiceMock = mock[PostService[IO]]
  private val secondServiceMock = mock[PostService[IO]]

  "PostService combination" - {
    "return combinedResult on success" in {
      firstServiceMock.post _ expects (group, post) returns IO.pure(firstResult)
      secondServiceMock.post _ expects (group, post) returns IO.pure(secondResult)

      PostService.combinePostServices(firstServiceMock, secondServiceMock)
        .post(group, post)
        .asserting(_ shouldBe combinedResult)
    }

    "drop result on exception" in {
      firstServiceMock.post _ expects(group, post) returns IO.pure(firstResult)
      secondServiceMock.post _ expects(group, post) returns IO.raiseError(new IOException())

      PostService.combinePostServices(firstServiceMock, secondServiceMock)
        .post(group, post)
        .asserting(_ shouldBe firstResult)
    }
  }

}
