package io.github.oybek.adjutant.service.qlang

import atto.Atto._
import atto.Parser
import cats.data.NonEmptyList
import enumeratum.{Enum, EnumEntry}
import io.github.oybek.adjutant.model.{Build, BuildType, Command, MatchUp, UnitType}

object CommandParser {
  def parseBuildId(text: String): Either[String, Int] =
    buildIdParser.parse(text).done.either

  def parseQuery(text: String): Either[String, QueryLang.Expr] =
    QueryLangParser.parse(text)

  def parseBuild(text: String): Either[String, (Build, NonEmptyList[Command])] =
    buildParser.parse(text).done.either

  def parseStartOrHelp(text: String): Either[String, String] =
    startOrHelpParser.parse(text).done.either

  def parseVoiceBuild(text: String): Either[String, Int] =
    parseVoiceBuild.parse(text).done.either

  //
  private lazy val parseVoiceBuild: Parser[Int] =
    stringCI("/pin") ~> int <~ endOfInput

  private lazy val startOrHelpParser: Parser[String] =
    (stringCI("/start") | stringCI("/help")) <~ endOfInput

  private lazy val buildIdParser: Parser[Int] =
    stringCI("/build") ~> int <~ endOfInput

  private lazy val queryParser: Parser[NonEmptyList[EnumEntry]] =
    opt(stringCI("/cmd")) ~>
    many1(
      many(whitespace) ~> (
        enumParser[BuildType] |
          enumParser[UnitType] |
          enumParser[MatchUp]
        )
    )

  private val buildParser =
    for {
      _ <- opt(stringCI("/cmd"))
      _ <- many(whitespace)
      matchUp <- enumParser[MatchUp]
      _ <- many1(whitespace)
      buildType <- enumParser[BuildType]
      _ <- many1(whitespace)
      patch <- stringOf(oneOf("0123456789."))
      commands <- many1(commandParser)
      commandsSorted = commands.sortBy(_.whenDo)
      build = Build(
        matchUp = matchUp,
        ttype = buildType,
        patch = patch,
        duration = commandsSorted.last.whenDo
      )
    } yield (build, commandsSorted)

  private lazy val commandParser =
    for {
      _ <- many1(whitespace)
      supply <- int
      _ <- many1(whitespace)
      whenDo <- timeParser
      _ <- many1(whitespace)
      whatDo <- takeWhile1(c => c != '\n' && c != '\r')
    } yield Command(supply = supply, whenDo = whenDo, whatDo = whatDo.trim.toLowerCase)

  private lazy val timeParser = {
    for {
      minutes <- int
      _ <- char(':')
      seconds <- int
    } yield minutes * 60 + seconds
  }
}
