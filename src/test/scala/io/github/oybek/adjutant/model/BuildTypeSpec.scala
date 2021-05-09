package io.github.oybek.adjutant.model

import io.github.oybek.adjutant.model.BuildType._
import io.github.oybek.adjutant.model.MatchUp.ZvT
import munit.FunSuite

class BuildTypeSpec extends FunSuite {

  test("parse from string") {
    assertEquals(
      Seq("Allin", "Cheese", "eCoNomic", "timingAttack").map(BuildType.withNameInsensitive),
      Seq(Allin, Cheese, Economic, TimingAttack)
    )
  }

  test("parse from string with fail".fail) {
    assertEquals(
      Seq("Allin", "Cheese", "eCoomic", "timingAttack").map(BuildType.withNameInsensitive),
      Seq(Allin, Cheese, Economic, TimingAttack)
    )
  }

  test("generic serialize") {
    assertEquals(
      Seq(ZvT, Allin).map(_.toString.toLowerCase),
      Seq("zvt", "allin")
    )
  }
}
