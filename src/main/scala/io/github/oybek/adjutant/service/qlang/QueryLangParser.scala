package io.github.oybek.adjutant.service.qlang

import atto.Atto._
import atto.Parser
import cats.implicits.catsSyntaxEitherId
import io.github.oybek.adjutant.model.{BuildType, MatchUp}
import io.github.oybek.adjutant.service.qlang.QueryLang._

object QueryLangParser {
  lazy val const: Parser[Const] =
    Seq(MatchUp, BuildType)
      .map(enumParser(_))
      .reduce(_ | _)
      .map(Const)

  lazy val lessExpr: Parser[Less] =
    (char('<') ~> int).map(Less)

  lazy val sumExpr: Parser[Expr] = {
    val nonEmptySum =
      for {
        a <- mulExpr
        _ <- char('|')
        b <- sumExpr
      } yield Or(a, b)
    nonEmptySum | mulExpr
  }

  lazy val mulExpr: Parser[Expr] = {
    val nonEmptyMul =
      for {
        a <- negateExpr
        _ <- char('&')
        b <- mulExpr
      } yield And(a, b)
    nonEmptyMul | negateExpr
  }

  lazy val negateExpr: Parser[Expr] = {
    val negExpr =
      for {
        _ <- char('!')
        a <- exprInParen
      } yield Not(a)
    negExpr | exprInParen
  }

  lazy val exprInParen: Parser[Expr] = {
    val expr1 =
      for {
        _ <- char('(')
        a <- sumExpr
        _ <- char(')')
      } yield a
    expr1 | const | lessExpr
  }

  def parse(raw: String): Either[String, Expr] = {
    val minRaw = raw.filterNot(_.isWhitespace)
    if (minRaw.length > 40) {
      "The query expression is too large".asLeft[Expr]
    } else {
      sumExpr.parse(minRaw).done.either
    }
  }
}
