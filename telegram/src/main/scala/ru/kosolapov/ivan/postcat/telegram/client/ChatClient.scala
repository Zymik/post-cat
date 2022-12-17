package ru.kosolapov.ivan.postcat.telegram.client

import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramUserId}
import telegramium.bots.{ChatId, ChatMember, Message}

trait ChatClient[F[_]] {

  def getMember(channelId: TelegramChannelId, userId: TelegramUserId): F[ChatMember]

  def sendMessage(channelId: TelegramChannelId, text: String): F[Message]

  def reply(replyTo: Message, text: String): F[Message]

  def getBotInChat(channelId: TelegramChannelId): F[ChatMember]
}
