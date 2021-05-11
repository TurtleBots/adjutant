package io.github.oybek.adjutant.service.impl

import atto.Atto._
import atto.Parser
import cats.data.NonEmptyList
import enumeratum.{Enum, EnumEntry}
import io.github.oybek.adjutant.model.{Build, BuildType, Command, MatchUp, UnitType}
import io.github.oybek.adjutant.service.ParserService

class ParserServiceImpl extends ParserService {
  override def parseBuildId(text: String): Either[String, Int] =
    buildIdParser.parse(text).done.either

  override def parseQuery(text: String): Either[String, NonEmptyList[EnumEntry]] =
    queryParser.parse(text).done.either

  override def parseBuild(text: String): Either[String, (Build, NonEmptyList[Command])] =
    buildParser.parse(text).done.either

  override def parseStartOrHelp(text: String): Either[String, String] =
    startOrHelpParser.parse(text).done.either

  override def parseAll(text: String): Either[String, String] =
    allParser.parse(text).done.either

  override def parseVoiceBuild(text: String): Either[String, Int] =
    parseVoiceBuild.parse(text).done.either

  //
  private lazy val parseVoiceBuild: Parser[Int] =
    stringCI("/pin2build") ~> int

  private lazy val startOrHelpParser: Parser[String] =
    stringCI("/start") | stringCI("/help")

  private lazy val allParser: Parser[String] =
    opt(stringCI("/cmd")) ~> many(whitespace) ~> stringCI("all") <~ endOfInput

  private lazy val buildIdParser: Parser[Int] =
    many(whitespace) ~>
    stringCI("/build") ~>
    int

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

  private def enumParser[T <: EnumEntry](implicit enum: Enum[T]): Parser[T] =
    enum
      .values
      .map(x => stringCI(x.toString.toLowerCase).map(_ => x))
      .reduce(_ | _)

  private lazy val timeParser = {
    for {
      minutes <- int
      _ <- char(':')
      seconds <- int
    } yield minutes * 60 + seconds
  }
}
