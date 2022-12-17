package ru.kosolapov.ivan.postcat.api.service.group

import cats.Functor
import cats.syntax.all._
import ru.kosolapov.ivan.postcat.common.domain.group.GroupId
import ru.kosolapov.ivan.postcat.common.dto.ApiError
import ru.kosolapov.ivan.postcat.api.repository.group.GroupRepository

class GroupValidationServiceImpl[F[_] : Functor]
(
  groupRepository: GroupRepository[F]
) extends GroupValidationService[F] {

  override def validateGroupPublicity(groupId: GroupId): F[Either[ApiError, GroupId]] =
    groupRepository.isPublicGroupId(groupId)
      .map {
        case true => Right(groupId)
        case false => Left(ApiError(s"Group $groupId is closed for api"))
      }
}
