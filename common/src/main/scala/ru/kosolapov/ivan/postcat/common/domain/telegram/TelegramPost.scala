package ru.kosolapov.ivan.postcat.common.domain.telegram

import ru.kosolapov.ivan.postcat.common.domain.post.Post

case class TelegramPost(authorId: TelegramUserId, post: Post, channelIds: List[TelegramChannelId])
