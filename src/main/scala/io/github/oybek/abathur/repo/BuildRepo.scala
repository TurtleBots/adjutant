package io.github.oybek.abathur.repo

import io.github.oybek.abathur.model.{Build, MatchUp}

trait BuildRepo[DB[_]] {
  def get(id: Int): DB[Option[Build]]
  def get(matchUp: MatchUp): DB[Seq[Build]]

  def add(build: Build): DB[Int]

  def thumbUp(id: Int): DB[Unit]
  def thumbDown(id: Int): DB[Unit]

  def setDictationTgId(id: Int, dictationTgId: String): DB[Int]
}
