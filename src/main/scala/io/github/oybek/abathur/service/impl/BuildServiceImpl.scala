package io.github.oybek.abathur.service.impl

import cats.data.{NonEmptyList, NonEmptySeq}
import cats.implicits._
import cats.{Monad, ~>}
import enumeratum.EnumEntry
import io.github.oybek.abathur.model.{Build, BuildType, Command, MatchUp, UnitType}
import io.github.oybek.abathur.repo.{BuildRepo, CommandRepo}
import io.github.oybek.abathur.service.BuildService

class BuildServiceImpl[F[_], DB[_]: Monad](buildRepo: BuildRepo[DB],
                                           commandRepo: CommandRepo[DB])
                                          (implicit dbRun: DB ~> F) extends BuildService[F] {

  override def addBuild(build: Build, commands: NonEmptyList[Command]): F[Unit] =
    dbRun {
      for {
        buildId <- buildRepo.add(build)
        _ <- commandRepo.add(
          commands
            .map(_.copy(buildId = buildId))
            .toList
        )
      } yield ()
    }

  override def getBuild(buildId: Int): F[Option[(Build, Seq[Command])]] =
    dbRun {
      for {
        build <- buildRepo.get(buildId)
        commands <- commandRepo.get(buildId)
      } yield build.map(_ -> commands)
    }

  override def getBuilds(query: Seq[EnumEntry]): F[Seq[Build]] = {
    val matchUpOpt = query.collectFirst { case x: MatchUp => x }
    val unitTypeOpt = query.collectFirst { case x: UnitType => x }
    val buildTypeOpt = query.collectFirst { case x: BuildType => x }
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
