package ru.kosolapov.ivan.postcat.core.repository.telegram.group

import ru.kosolapov.ivan.postcat.common.domain.group.GroupId
import ru.kosolapov.ivan.postcat.common.domain.telegram.TelegramChannelId

trait TelegramChannelGroupRepository[F[_]] {

  /**
   * Add channels to group
   */
  def addChannels(group: GroupId, channels: Set[TelegramChannelId]): F[Unit]

  /**
   * Get telegram channels associated with [[TelegramChannelId]]
   * @return list of associated channels
   */
  def getTelegramChannelsByGroup(groupId: GroupId): F[List[TelegramChannelId]]

}
