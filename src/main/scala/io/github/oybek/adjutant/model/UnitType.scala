package io.github.oybek.adjutant.model

import enumeratum._

sealed trait UnitType extends EnumEntry

object UnitType extends Enum[UnitType] {
  case object Adept extends UnitType
  case object Archon extends UnitType
  case object Carrier extends UnitType
  case object Colossus extends UnitType
  case object DarkTemplar extends UnitType
  case object Disruptor extends UnitType
  case object HighTemplar extends UnitType
  case object Immortal extends UnitType
  case object Interceptor extends UnitType
  case object Mothership extends UnitType
  case object Observer extends UnitType
  case object Oracle extends UnitType
  case object Phoenix extends UnitType
  case object Probe extends UnitType
  case object Sentry extends UnitType
  case object Stalker extends UnitType
  case object Tempest extends UnitType
  case object VoidRay extends UnitType
  case object WarpPrism extends UnitType
  case object Zealot extends UnitType

  case object Banshee extends UnitType
  case object BattleCruiser extends UnitType
  case object Cyclone extends UnitType
  case object Ghost extends UnitType
  case object Hellbat extends UnitType
  case object Hellion extends UnitType
  case object Liberator extends UnitType
  case object MULE extends UnitType
  case object Marauder extends UnitType
  case object Marine extends UnitType
  case object Medivac extends UnitType
  case object Raven extends UnitType
  case object Reaper extends UnitType
  case object SCV extends UnitType
  case object SiegeTank extends UnitType
  case object Thor extends UnitType
  case object Viking extends UnitType
  case object WidowMine extends UnitType

  case object Baneling extends UnitType
  case object Broodling extends UnitType
  case object Broodlord extends UnitType
  case object Changeling extends UnitType
  case object Cocoon extends UnitType
  case object Corruptor extends UnitType
  case object Drone extends UnitType
  case object Hydralisk extends UnitType
  case object Infestor extends UnitType
  case object Larva extends UnitType
  case object Locust extends UnitType
  case object Lurker extends UnitType
  case object Mutalisk extends UnitType
  case object Overlord extends UnitType
  case object Overseer extends UnitType
  case object Queen extends UnitType
  case object Ravager extends UnitType
  case object Roach extends UnitType
  case object SwarmHost extends UnitType
  case object Ultralisk extends UnitType
  case object Viper extends UnitType
  case object Zergling extends UnitType

  val values: IndexedSeq[UnitType] = findValues
}

