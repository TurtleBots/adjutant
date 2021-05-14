package io.github.oybek.adjutant.service.build

import cats.data.NonEmptyList
import enumeratum.EnumEntry
import io.github.oybek.adjutant.model.{Build, Command}
import io.github.oybek.adjutant.service.qlang.QueryLang

trait BuildService[F[_]] {
  def getBuildById(buildId: Int): F[Option[(Build, Seq[Command])]]
  def getBuildsByQuery(query: QueryLang.Expr): F[Seq[Build]]
  def addBuild(build: Build, commands: NonEmptyList[Command]): F[Int]
  def setDictationTgId(buildId: Int, tgId: String): F[Int]
  def getBuildCount: F[Int]
}
