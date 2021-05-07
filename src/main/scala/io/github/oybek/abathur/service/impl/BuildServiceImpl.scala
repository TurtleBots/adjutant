package io.github.oybek.abathur.service.impl

import cats.data.NonEmptySeq
import cats.implicits._
import cats.{Monad, ~>}
import io.github.oybek.abathur.model.{Build, BuildType, Command, MatchUp, UnitType}
import io.github.oybek.abathur.repo.{BuildRepo, CommandRepo}
import io.github.oybek.abathur.service.BuildService

class BuildServiceImpl[F[_], DB[_]: Monad](buildRepo: BuildRepo[DB],
                                           commandRepo: CommandRepo[DB])
                                          (implicit dbRun: DB ~> F) extends BuildService[F] {

  override def getBuild(buildId: Int): F[Option[(Build, Seq[Command])]] =
    dbRun {
      for {
        build <- buildRepo.get(buildId)
        commands <- commandRepo.get(buildId)
      } yield build.map(_ -> commands)
    }

  override def getBuilds(query: String): F[Seq[Build]] = {
    val enums = query
      .split(",")
      .map(_.trim)
      .flatMap { x =>
        BuildType.withNameInsensitiveOption(x)
          .orElse(MatchUp.withNameInsensitiveOption(x))
          .orElse(UnitType.withNameInsensitiveOption(x))
      }
    val matchUpOpt = enums.collectFirst { case x: MatchUp => x }
    val unitTypeOpt = enums.collectFirst { case x: UnitType => x }
    val buildTypeOpt = enums.collectFirst { case x: BuildType => x }
    dbRun {
      for {
        buildIds <- unitTypeOpt.fold(Seq.empty[Int].pure[DB])(commandRepo.getBuildIds)
        builds <- buildRepo.get(
          matchUpOpt,
          buildTypeOpt,
          NonEmptySeq.fromSeq(buildIds)
        )
      } yield builds
    }
  }
}
