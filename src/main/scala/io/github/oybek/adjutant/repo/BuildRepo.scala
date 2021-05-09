package io.github.oybek.adjutant.repo

import cats.data.NonEmptySeq
import io.github.oybek.adjutant.model.{Build, BuildType, MatchUp}

trait BuildRepo[DB[_]] {
  def get(id: Int): DB[Option[Build]]
  def get(matchUp: Option[MatchUp],
          buildType: Option[BuildType],
          ids: Option[NonEmptySeq[Int]]): DB[Seq[Build]]

  def add(build: Build): DB[Int]

  def thumbUp(id: Int): DB[Unit]
  def thumbDown(id: Int): DB[Unit]

  def setDictationTgId(id: Int, dictationTgId: String): DB[Int]
}
