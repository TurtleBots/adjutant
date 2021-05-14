package io.github.oybek.adjutant.service.qlang

import enumeratum.EnumEntry

object QueryLang {
  sealed trait Expr
  case class And(a: Expr, b: Expr) extends Expr
  case class Const(enumEntry: EnumEntry) extends Expr
  case class Less(minute: Int) extends Expr
  case class Not(expr: Expr) extends Expr
  case class Or(a: Expr, b: Expr) extends Expr
}
