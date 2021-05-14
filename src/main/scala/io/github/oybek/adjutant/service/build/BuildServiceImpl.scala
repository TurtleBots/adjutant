package io.github.oybek.adjutant.service.build

import cats.data.{NonEmptyList, NonEmptySeq}
import cats.implicits._
import cats.{Monad, ~>}
import enumeratum.EnumEntry
import io.github.oybek.adjutant.model.{Build, BuildType, Command, Journal, MatchUp, UnitType}
import io.github.oybek.adjutant.repo.{BuildRepo, CommandRepo, JournalRepo}
import io.github.oybek.adjutant.service.qlang.QueryLang

class BuildServiceImpl[F[_], DB[_]: Monad](implicit
                                           buildRepo: BuildRepo[DB],
                                           commandRepo: CommandRepo[DB],
                                           dbRun: DB ~> F) extends BuildService[F] {

  override def getBuildCount: F[Int] =
    dbRun(buildRepo.getBuildCount)

  override def addBuild(build: Build, commands: NonEmptyList[Command]): F[Int] =
    dbRun {
      for {
        buildId <- buildRepo.add(build)
        _ <- commandRepo.add(
          commands
            .map(_.copy(buildId = buildId))
            .toList
        )
      } yield buildId
    }

  override def getBuildById(buildId: Int): F[Option[(Build, Seq[Command])]] =
    dbRun {
      for {
        build <- buildRepo.getById(buildId)
        commands <- commandRepo.get(buildId)
      } yield build.map(_ -> commands)
    }

  override def getBuildsByQuery(query: QueryLang.Expr): F[Seq[Build]] =
    dbRun {
      buildRepo.getByQuery(query)
    }

  override def setDictationTgId(buildId: Int, tgId: String): F[Int] =
    dbRun(buildRepo.setDictationTgId(buildId, tgId))
}
