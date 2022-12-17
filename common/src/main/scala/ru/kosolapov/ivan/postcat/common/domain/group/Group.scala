package ru.kosolapov.ivan.postcat.common.domain.group

import ru.kosolapov.ivan.postcat.common.domain.user.User

case class Group(groupId: GroupId, owner: User, name: String)
