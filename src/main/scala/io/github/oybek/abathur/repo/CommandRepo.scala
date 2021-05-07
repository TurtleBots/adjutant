package io.github.oybek.abathur.repo

import io.github.oybek.abathur.model.{Command, UnitType}

trait CommandRepo[DB[_]] {
  def get(buildId: Int): DB[Seq[Command]]
  def add(commands: Seq[Command]): DB[Unit]
  def getBuildIds(unitType: UnitType): DB[Seq[Int]]
}
