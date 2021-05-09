package io.github.oybek.adjutant.service

import cats.data.NonEmptyList
import enumeratum.EnumEntry
import io.github.oybek.adjutant.model.{Build, Command}

trait BuildService[F[_]] {
  def getBuild(buildId: Int): F[Option[(Build, Seq[Command])]]
  def getBuilds(query: Seq[EnumEntry]): F[Seq[Build]]
  def addBuild(build: Build, commands: NonEmptyList[Command]): F[Unit]
  def setDictationTgId(buildId: Int, tgId: String): F[Int]
}
