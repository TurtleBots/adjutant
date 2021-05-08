package io.github.oybek.abathur.repo.impl

import enumeratum.SlickEnumSupport
import io.github.oybek.abathur.model.Journal
import io.github.oybek.abathur.repo.JournalRepo
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import java.sql.Timestamp
import scala.concurrent.ExecutionContext

class JournalRepoImpl(implicit executionContext: ExecutionContext) extends JournalRepo[DBIO] with SlickEnumSupport {

  override def add(journal: Journal): DBIO[Unit] =
    (journalTable += journal).map(_ => ())

  //
  override val profile = PostgresProfile

  private class JournalTable(tag: Tag) extends Table[Journal](tag, "journal") {
    def userId    = column[Long]("user_id")
    def buildId   = column[Int]("build_id")
    def timestamp = column[Timestamp]("timestamp")

    def * = (userId, buildId, timestamp).mapTo[Journal]
  }

  private val journalTable = TableQuery[JournalTable]
}
