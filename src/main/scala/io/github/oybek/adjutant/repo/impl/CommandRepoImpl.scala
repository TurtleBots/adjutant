package io.github.oybek.adjutant.repo.impl

import enumeratum.SlickEnumSupport
import io.github.oybek.adjutant.model.{Command, UnitType}
import io.github.oybek.adjutant.repo.CommandRepo
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

class CommandRepoImpl(implicit executionContext: ExecutionContext) extends CommandRepo[DBIO] with SlickEnumSupport {

  override def get(buildId: Int): DBIO[Seq[Command]] =
    commandTable
      .filter(_.buildId === buildId.bind)
      .sortBy(_.whenDo)
      .result

  override def add(commands: Seq[Command]): DBIO[Unit] =
    (commandTable ++= commands).map(_ => ())

  override def getBuildIds(unitType: UnitType): DBIO[Seq[Int]] =
    commandTable
      .filter(_.whatDo.toLowerCase like s"%${unitType.toString.toLowerCase}%".bind)
      .map(_.buildId)
      .distinct
      .result

  //
  override val profile = PostgresProfile

  private class CommandTable(tag: Tag) extends Table[Command](tag, "command") {
    def supply  = column[Int]("supply")
    def whenDo  = column[Int]("when_do")
    def whatDo  = column[String]("what_do")
    def buildId = column[Int]("build_id")

    def * = (
      supply,
      whenDo,
      whatDo,
      buildId
    ).mapTo[Command]
  }

  private val commandTable = TableQuery[CommandTable]
}
