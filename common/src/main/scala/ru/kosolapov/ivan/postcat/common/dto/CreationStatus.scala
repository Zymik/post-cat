package ru.kosolapov.ivan.postcat.common.dto

sealed trait CreationStatus

/**
 * Enum that used to show status of creation operation.
 * For example, when user try to register for the first time it should return [[CreationStatus.Created]],
 * if user try to do it, when he already registered, it should return [[CreationStatus.Exist]]
 */
object CreationStatus {
  case object Created   extends CreationStatus

  case object Exist extends CreationStatus

  def fromCount(count: Int): CreationStatus = if (count == 0) Exist else Created
}