package io.github.oybek.abathur.service

import cats.data.NonEmptyList
import io.github.oybek.abathur.model.{Build, Command}

trait BuildService[F[_]] {
  def getBuild(buildId: Int): F[Option[(Build, Seq[Command])]]
  def getBuilds(query: String): F[Seq[Build]]
  def addBuild(build: Build, commands: NonEmptyList[Command]): F[Unit]
}
