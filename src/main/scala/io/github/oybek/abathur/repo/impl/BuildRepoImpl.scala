package io.github.oybek.abathur.repo.impl

import cats.data.NonEmptySeq
import enumeratum.SlickEnumSupport
import io.github.oybek.abathur.model.{Build, BuildType, MatchUp}
import io.github.oybek.abathur.repo.BuildRepo
import slick.ast.BaseTypedType
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

class BuildRepoImpl(implicit executionContext: ExecutionContext) extends BuildRepo[DBIO] with SlickEnumSupport {
  override def get(id: Int): DBIO[Option[Build]] =
    buildTable
      .filter(_.id === id.bind)
      .result
      .headOption

  override def get(matchUpOpt: Option[MatchUp],
                   buildTypeOpt: Option[BuildType],
                   ids: Option[NonEmptySeq[Int]]): DBIO[Seq[Build]] =
    buildTable
      .filterOpt(ids)((build, ids) => build.id inSetBind ids.toSeq)
      .filterOpt(matchUpOpt)((build, matchUp) => build.matchUp === matchUp.bind)
      .filterOpt(buildTypeOpt)((build, buildType) => build.ttype === buildType.bind)
      .result

  override def add(build: Build): DBIO[Int] =
    buildTable.returning(buildTable.map(_.id)) += build

  override def thumbUp(id: Int): DBIO[Unit] = {
    val thumbsUp = buildTable.filter(_.id === id.bind).map(_.thumbsUp)
    incInt(thumbsUp).transactionally
  }

  override def thumbDown(id: Int): DBIO[Unit] = {
    val thumbsDown = buildTable.filter(_.id === id.bind).map(_.thumbsDown)
    incInt(thumbsDown).transactionally
  }

  override def setDictationTgId(id: Int, dictationTgId: String): DBIO[Int] =
    buildTable
      .filter(_.id === id.bind)
      .map(_.dictationTgId)
      .update(dictationTgId)

  private def incInt(query: Query[Rep[Int], Int, Seq]): DBIO[Unit] =
    for {
      valueOpt <- query.result.headOption
      _ <- DBIO.sequenceOption(
        valueOpt.map { value =>
          query.update(value + 1)
        }
      )
    } yield ()

  //
  override val profile = PostgresProfile

  private class BuildTable(tag: Tag) extends Table[Build](tag, "build") {
    def id             = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def matchUp        = column[MatchUp]("matchup")
    def duration       = column[Int]("duration")
    def ttype          = column[BuildType]("ttype")
    def patch          = column[String]("patch")
    def author         = column[String]("author")
    def thumbsUp       = column[Int]("thumbs_up")
    def thumbsDown     = column[Int]("thumbs_down")
    def dictationTgId  = column[String]("dictation_tg_id")

    def * = (
      id,
      matchUp,
      duration,
      ttype,
      patch,
      author.?,
      thumbsUp,
      thumbsDown,
      dictationTgId.?).mapTo[Build]
  }

  private val buildTable = TableQuery[BuildTable]

  implicit lazy val matchUpMapper: BaseTypedType[MatchUp] = mappedColumnTypeForLowercaseEnum(MatchUp)
  implicit lazy val buildTypeMapper: BaseTypedType[BuildType] = mappedColumnTypeForLowercaseEnum(BuildType)
}
