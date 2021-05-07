package io.github.oybek.abathur.service

import io.github.oybek.abathur.model.{Build, Command}

trait BuildService[F[_]] {
  def getBuild(buildId: Int): F[Option[(Build, Seq[Command])]]
  def getBuilds(query: String): F[Seq[Build]]
}
