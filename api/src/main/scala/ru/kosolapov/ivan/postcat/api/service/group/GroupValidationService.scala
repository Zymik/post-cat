package ru.kosolapov.ivan.postcat.api.service.group

import ru.kosolapov.ivan.postcat.common.domain.group.GroupId
import ru.kosolapov.ivan.postcat.common.dto.ApiError

trait GroupValidationService[F[_]] {

  /**
   * Validate that group are public to rest api
   * @return [[Left]] with [[ApiError]] if group is not public, else [[Right]] with passed group id
   */
  def validateGroupPublicity(groupId: GroupId) : F[Either[ApiError, GroupId]]

}
