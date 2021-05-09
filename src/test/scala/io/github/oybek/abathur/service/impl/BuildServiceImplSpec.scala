package io.github.oybek.abathur.service.impl

import cats.data.NonEmptySeq
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxOptionId}
import cats.{Id, ~>}
import enumeratum.EnumEntry
import io.github.oybek.abathur.model.{BuildType, Donors, MatchUp}
import io.github.oybek.abathur.repo.{BuildRepo, CommandRepo}
import munit.FunSuite
import org.scalamock.scalatest.MockFactory

class BuildServiceImplSpec extends FunSuite with MockFactory with Donors {

  test("buildServiceImplSpec.getBuild") {
    inSequence {
      (buildRepo.get(_: Int))
        .expects(1)
        .returning(buildDonor.some.pure[Id])
        .once()

      (commandRepo.get _)
        .expects(1)
        .returning(commandsDonor.toList.pure[Id])
        .once()
    }

    assertEquals(
      buildServiceImpl.getBuild(1),
      (buildDonor, commandsDonor.toList).some.pure[Id]
    )
  }

  test("buildServiceImplSpec.getBuilds") {
    inSequence {
      (buildRepo.get(_: Option[MatchUp], _: Option[BuildType], _: Option[NonEmptySeq[Int]]))
        .expects(buildDonor.matchUp.some, buildDonor.ttype.some, Option.empty[NonEmptySeq[Int]])
        .returning(Seq(buildDonor))
        .once()
    }
    assertEquals(
      buildServiceImpl.getBuilds(Seq(buildDonor.matchUp, buildDonor.ttype)),
      Seq(buildDonor).pure[Id]
    )
  }

  test("buildServiceImplSpec.getBuilds") {
    inSequence {
      (buildRepo.add _)
        .expects(buildDonor)
        .returning(buildDonor.id)
        .once()

      (commandRepo.add _)
        .expects(commandsDonor.map(_.copy(buildId = buildDonor.id)).toList)
        .returning(().pure[Id])
        .once()
    }

    assertEquals(
      buildServiceImpl.addBuild(buildDonor, commandsDonor),
      ().pure[Id]
    )
  }

  private implicit lazy val dbRun: Id ~> Id = new ~>[Id, Id] {
    override def apply[A](fa: Id[A]): Id[A] = fa
  }
  private lazy val buildRepo = mock[BuildRepo[Id]]
  private lazy val commandRepo = mock[CommandRepo[Id]]
  private lazy val buildServiceImpl = new BuildServiceImpl[Id, Id](buildRepo, commandRepo)
}
