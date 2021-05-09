package io.github.oybek.adjutant.model

import enumeratum._

sealed trait MatchUp extends EnumEntry

object MatchUp extends Enum[MatchUp] {
  case object TvZ extends MatchUp
  case object TvT extends MatchUp
  case object TvP extends MatchUp
  case object PvZ extends MatchUp
  case object PvT extends MatchUp
  case object PvP extends MatchUp
  case object ZvZ extends MatchUp
  case object ZvT extends MatchUp
  case object ZvP extends MatchUp
  case object TvX extends MatchUp
  case object PvX extends MatchUp
  case object ZvX extends MatchUp
  case object Unknown extends MatchUp

  val values: IndexedSeq[MatchUp] = findValues
}
