package io.github.oybek.adjutant.model

import enumeratum._

sealed trait BuildType extends EnumEntry

object BuildType extends Enum[BuildType] {
  case object Allin extends BuildType
  case object Cheese extends BuildType
  case object TimingAttack extends BuildType
  case object Economic extends BuildType
  case object Unknown extends BuildType

  val values: IndexedSeq[BuildType] = findValues
}
