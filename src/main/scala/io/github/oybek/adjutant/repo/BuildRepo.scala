package io.github.oybek.adjutant.repo

import io.github.oybek.adjutant.model.{Build, BuildType, MatchUp}
import io.github.oybek.adjutant.service.qlang.QueryLang

trait BuildRepo[DB[_]] {
  def getBuildCount: DB[Int]
  def getById(id: Int): DB[Option[Build]]
  def getByQuery(expr: QueryLang.Expr): DB[Seq[Build]]

  def add(build: Build): DB[Int]

  def thumbUp(id: Int): DB[Unit]
  def thumbDown(id: Int): DB[Unit]

  def setDictationTgId(id: Int, dictationTgId: String): DB[Int]
}
