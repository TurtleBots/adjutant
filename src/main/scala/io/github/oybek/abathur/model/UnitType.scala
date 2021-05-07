package io.github.oybek.abathur.model

import enumeratum._

sealed trait UnitType extends EnumEntry

object UnitType extends Enum[UnitType] {
  case object CSV extends UnitType

  val values: IndexedSeq[UnitType] = findValues
}

