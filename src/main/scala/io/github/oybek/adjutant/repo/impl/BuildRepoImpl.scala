package io.github.oybek.adjutant.repo.impl

import cats.data.NonEmptySeq
import enumeratum.SlickEnumSupport
import io.github.oybek.adjutant.model.{Build, BuildType, MatchUp}
import io.github.oybek.adjutant.repo.BuildRepo
import io.github.oybek.adjutant.service.qlang.QueryLang
import io.github.oybek.adjutant.service.qlang.QueryLang.{And, Const, Expr, Less, Not, Or}
import slick.ast.BaseTypedType
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

class BuildRepoImpl(implicit executionContext: ExecutionContext) extends BuildRepo[DBIO] with SlickEnumSupport {
  override def getBuildCount: DBIO[Int] =
    buildTable.length.result

  override def getById(id: Int): DBIO[Option[Build]] =
    buildTable
      .filter(_.id === id.bind)
      .result
      .headOption

  override def getByQuery(expr: QueryLang.Expr): DBIO[Seq[Build]] = {
    val sql =
      buildTable.filter(table => generateSql(expr)(table)).result
    println("xxx" + sql.statements.mkString)
    sql
  }

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

  private def generateSql(expr: Expr)(implicit buildTable: BuildTable): Rep[Boolean] =
    expr match {
      case Const(matchUp: MatchUp) => buildTable.matchUp === matchUp.bind
      case Const(buildType: BuildType) => buildTable.ttype === buildType.bind
      case Const(_) => true.bind
      case Less(minutes) => buildTable.duration < (minutes * 60).bind
      case Or(a, b) => generateSql(a) || generateSql(b)
      case And(a, b) => generateSql(a) && generateSql(b)
      case Not(expr) => !generateSql(expr)
    }

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

  class BuildTable(tag: Tag) extends Table[Build](tag, "build") {
    def id             = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def matchUp        = column[MatchUp]("matchup")
    def duration       = column[Int]("duration")
    def ttype          = column[BuildType]("ttype")
    def patch          = column[String]("patch")
    def author         = column[Long]("author")
    def thumbsUp       = column[Int]("thumbs_up")
    def thumbsDown     = column[Int]("thumbs_down")
    def dictationTgId  = column[String]("dictation_tg_id")

    def * = (
      id,
      matchUp,
      duration,
      ttype,
      patch,
      author,
      thumbsUp,
      thumbsDown,
      dictationTgId.?).mapTo[Build]
  }

  val buildTable = TableQuery[BuildTable]

  implicit lazy val matchUpMapper: BaseTypedType[MatchUp] = mappedColumnTypeForLowercaseEnum(MatchUp)
  implicit lazy val buildTypeMapper: BaseTypedType[BuildType] = mappedColumnTypeForLowercaseEnum(BuildType)
}
