package io.github.oybek.adjutant.model

import io.github.oybek.adjutant.model.MatchUp._
import munit.FunSuite

class MatchUpSpec extends FunSuite {

  test("parse from string") {
    assertEquals(
      Seq("TVZ", "TvT", "tvt", "zvp", "zvP").map(MatchUp.withNameInsensitive),
      Seq(TvZ, TvT, TvT, ZvP, ZvP)
    )
  }
}
