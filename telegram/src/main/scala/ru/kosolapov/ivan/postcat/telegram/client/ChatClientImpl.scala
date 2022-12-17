package ru.kosolapov.ivan.postcat.telegram.client

import ru.kosolapov.ivan.postcat.common.domain.telegram.{TelegramChannelId, TelegramUserId}
import telegramium.bots.high.{Api, Methods}
import telegramium.bots.{ChatIntId, ChatMember, ChatStrId, Message}
import telegramium.bots.high.implicits._

class ChatClientImpl[F[_]](botId: TelegramUserId)(implicit api: Api[F]) extends ChatClient[F] {
  override def getMember(channelId: TelegramChannelId, userId: TelegramUserId): F[ChatMember] =
    Methods.getChatMember(ChatStrId(channelId.id), userId.id).exec

  override def sendMessage(channelId: TelegramChannelId, text: String): F[Message] =
    Methods.sendMessage(ChatStrId(channelId.id), text).exec

  override def reply(replyTo: Message, text: String): F[Message] =
    Methods.sendMessage(ChatIntId(replyTo.chat.id), text, replyToMessageId = Some(replyTo.messageId)).exec
    

  override def getBotInChat(channelId: TelegramChannelId): F[ChatMember] =
    Methods.getChatMember(ChatStrId(channelId.id), botId.id).exec
}
