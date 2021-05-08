package io.github.oybek.abathur.service.impl

import cats.implicits.catsSyntaxEitherId
import munit.FunSuite

class ParserServiceSpec extends FunSuite {
  val parserService = new ParserServiceImpl

  test("parseBuildIdSpec") {
    assertEquals(
      clue(parserService.parseBuildId("/build123")),
      clue(123.asRight[String])
    )

    assertEquals(
      clue(parserService.parseBuildId("/build1")),
      clue(1.asRight[String])
    )

    assertEquals(
      clue(parserService.parseBuildId("/build")),
      clue("Failure reading:bigInt".asLeft[Int])
    )
  }
}
