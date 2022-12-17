package ru.kosolapov.ivan.postcat.telegram

import org.http4s.{Header, Request}
import org.typelevel.ci.CIString
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId
import ru.kosolapov.ivan.postcat.common.endpoint.Headers
import telegramium.bots._

package object implicits {

  implicit class ChatMemberExt(val chatMember: ChatMember) {
    /**
     * Check that chat member have rights to post messages
     */
    def canPost: Boolean = chatMember match {
      case _: ChatMemberOwner => true
      case admin: ChatMemberAdministrator => admin.canPostMessages.getOrElse(false)
      case _ => false
    }
  }

  implicit class SecurityRequestExt[F[_]](val request: Request[F]) {
    /**
     * Put userId into headers of request
     */
    def putUser(userId: TelegramUserId): Request[F] = {
      request
        .putHeaders(
          Header.Raw(
            CIString(Headers.user), userId.id.toString
          )
        )
    }

    /**
     * Put groupName into headers of request
     */
    def putGroup(groupName: String): Request[F] = {
      request
        .putHeaders(
          Header.Raw(
            CIString(Headers.groupName), groupName
          )
        )
    }

  }

}
