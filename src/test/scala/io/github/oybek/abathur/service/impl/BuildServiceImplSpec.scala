package io.github.oybek.abathur.service.impl

import cats.data.NonEmptySeq
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxOptionId}
import cats.{Id, ~>}
import io.github.oybek.abathur.model.{Build, BuildType, Command, Donors, MatchUp, UnitType}
import io.github.oybek.abathur.repo.{BuildRepo, CommandRepo}
import munit.FunSuite

class BuildServiceImplSpec extends FunSuite with Donors {

  test("getBuildSpec") {
    assertEquals(
      buildServiceImpl.getBuild(1),
      (buildDonor, commandsDonor.toList).some
    )
  }

  test("getBuildsSpec") {
    assertEquals(
      buildServiceImpl.getBuilds(Seq(buildDonor.matchUp, buildDonor.ttype)),
      Seq(buildDonor)
    )
  }

  test("addBuildSpec") {
    assertEquals(
      buildServiceImpl.addBuild(buildDonor, commandsDonor),
      ()
    )
  }

  private implicit lazy val dbRun: Id ~> Id = new ~>[Id, Id] {
    override def apply[A](fa: Id[A]): Id[A] = fa
  }

  private lazy val buildRepo = new BuildRepo[Id] {
    override def add(build: Build): Id[Int] =
      if (build == buildDonor) 1.pure[Id]
      else ???

    override def get(matchUp: Option[MatchUp],
                     buildType: Option[BuildType],
                     ids: Option[NonEmptySeq[Int]]): Id[Seq[Build]] =
      (matchUp, buildType, ids) match {
        case (Some(_), Some(_), None) => Seq(buildDonor)
        case _ => ???
      }

    override def get(id: Int): Id[Option[Build]] =
      if (id == 1) buildDonor.some.pure[Id]
      else Option.empty[Build].pure[Id]

    override def setDictationTgId(id: Int, dictationTgId: String): Id[Int] = ???
    override def thumbDown(id: Int): Id[Unit] = ???
    override def thumbUp(id: Int): Id[Unit] = ???
  }

  private lazy val commandRepo = new CommandRepo[Id] {
    override def add(commands: Seq[Command]): Id[Unit] = ().pure[Id]

    override def get(buildId: Int): Id[Seq[Command]] = commandsDonor.toList.pure[Id]

    override def getBuildIds(unitType: UnitType): Id[Seq[Int]] = Seq.empty[Int].pure[Id]
  }
  private lazy val buildServiceImpl = new BuildServiceImpl[Id, Id](buildRepo, commandRepo)
}
