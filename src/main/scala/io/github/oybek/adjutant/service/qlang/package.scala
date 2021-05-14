package io.github.oybek.adjutant.service

import atto.Atto._
import atto.Parser
import enumeratum.{Enum, EnumEntry}

package object qlang {
  def enumParser[T <: EnumEntry](implicit enum: Enum[T]): Parser[T] =
    enum
      .values
      .map(x => stringCI(x.toString.toLowerCase).map(_ => x))
      .reduce(_ | _)
}
