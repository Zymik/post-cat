package ru.kosolapov.ivan.postcat.common.domain.user

import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramUserId

case class User(userId: UserId, telegramUserId: TelegramUserId)
