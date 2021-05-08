package io.github.oybek.abathur.service.impl

import atto.Atto._
import atto.Parser
import cats.data.NonEmptyList
import enumeratum.{Enum, EnumEntry}
import io.github.oybek.abathur.model.{Build, BuildType, Command, MatchUp, UnitType}
import io.github.oybek.abathur.service.ParserService

class ParserServiceImpl extends ParserService {
  override def parseBuildId(text: String): Either[String, Int] =
    buildIdParser.parse(text).done.either

  override def parseQuery(text: String): Either[String, Seq[EnumEntry]] =
    queryParser.parse(text).done.either

  override def parseBuild(text: String): Either[String, (Build, NonEmptyList[Command])] =
    buildParser.parse(text).done.either

  //
  private lazy val buildIdParser: Parser[Int] =
    many(whitespace) ~>
    stringCI("/build") ~>
    int

  private lazy val queryParser: Parser[List[EnumEntry]] =
    many(
      many(whitespace) ~> (
        enumParser[BuildType] |
          enumParser[UnitType] |
          enumParser[MatchUp]
        )
    )

  private val buildParser =
    for {
      _ <- many(whitespace)
      matchUp <- enumParser[MatchUp]
      _ <- many(whitespace)
      buildType <- enumParser[BuildType]
      _ <- many(whitespace)
      patch <- stringOf(oneOf("0123456789."))
      _ <- many(whitespace)
      author <- opt(stringOf(letterOrDigit))
      commands <- many1(commandParser)

      commandsSorted = commands.sortBy(_.whenDo)
      build = Build(
        matchUp = matchUp,
        ttype = buildType,
        patch = patch,
        author = author,
        duration = commandsSorted.last.whenDo
      )
    } yield (build, commandsSorted)

  private lazy val commandParser =
    for {
      _ <- many(whitespace)
      supply <- int
      _ <- many(horizontalWhitespace)
      whenDo <- timeParser
      _ <- many(horizontalWhitespace)
      whatDo <- takeWhile1(c => c != '\n' && c != '\r')
    } yield Command(supply = supply, whenDo = whenDo, whatDo = whatDo)

  private def enumParser[T <: EnumEntry](implicit enum: Enum[T]): Parser[T] =
    enum
      .values
      .map(x => stringCI(x.toString.toLowerCase).map(_ => x))
      .reduce(_ | _)

  private lazy val timeParser =
    for {
      minutes <- int
      _ <- char(':')
      seconds <- int
    } yield minutes * 60 + seconds
}
