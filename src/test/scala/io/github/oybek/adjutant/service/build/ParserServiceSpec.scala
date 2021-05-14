package io.github.oybek.adjutant.service.build

import cats.data.NonEmptyList
import cats.implicits.catsSyntaxEitherId
import io.github.oybek.adjutant.model.{Build, Command}
import io.github.oybek.adjutant.model.BuildType._
import io.github.oybek.adjutant.model.MatchUp._
import io.github.oybek.adjutant.model.UnitType.Roach
import io.github.oybek.adjutant.service.qlang.CommandParser
import io.github.oybek.adjutant.service.qlang.QueryLang.{And, Const, Less}
import munit.FunSuite

class ParserServiceSpec extends FunSuite {
  test("parseBuildIdSpec") {
    assertEquals(
      clue(CommandParser.parseBuildId("/build123")),
      clue(123.asRight[String])
    )

    assertEquals(
      clue(CommandParser.parseBuildId("/build1")),
      clue(1.asRight[String])
    )

    assertEquals(
      clue(CommandParser.parseBuildId("/build")),
      clue("Failure reading:bigInt".asLeft[Int])
    )
  }

  test("parseQuerySpec") {
    assertEquals(
      CommandParser.parseQuery("zvt & zvt & allin & zvz"),
      And(Const(ZvT),
        And(Const(ZvT),
          And(Const(Allin), Const(ZvZ)))).asRight[String]
    )

    assertEquals(
      CommandParser.parseQuery(
        """
          |zvt & allin
          |& <9
          |""".stripMargin),
      And(Const(ZvT), And(Const(Allin), Less(9))).asRight[String]
    )
  }

  test("parseBuildSpec") {
    assertEquals(
      CommandParser.parseBuild(rawBuild), (
        Build(
          matchUp = ZvT,
          duration = 6*60+12,
          ttype = Allin,
          patch = "4.11.0"
        ),
        NonEmptyList.of(
          Command(13, 16, "overlord"),
          Command(16, 41, "spawning pool"),
          Command(18, 1*60+13, "hatchery"),
          Command(18, 1*60+17, "extractor"),
          Command(18, 1*60+28, "queen"),
          Command(20, 1*60+33, "zergling x2"),
          Command(21, 1*60+44, "roach warren"),
          Command(20, 1*60+48, "overlord"),
          Command(21, 2*60+ 8, "queen"),
          Command(24, 2*60+26, "queen"),
          Command(24, 2*60+28, "roach x3"),
          Command(36, 2*60+50, "overlord"),
          Command(36, 3*60+ 7, "lair"),
          Command(44, 3*60+26, "overlord"),
          Command(44, 3*60+30, "extractor"),
          Command(48, 4*60+ 5, "extractor x2"),
          Command(48, 4*60+ 7, "glial reconstitution"),
          Command(46, 4*60+14, "overseer"),
          Command(46, 4*60+16, "roach"),
          Command(49, 4*60+21, "hatchery"),
          Command(49, 4*60+24, "overlord"),
          Command(49, 4*60+31, "roach"),
          Command(52, 4*60+44, "roach x4"),
          Command(60, 4*60+59, "roach"),
          Command(74, 5*60+15, "evolution chamber x2"),
          Command(99, 6*60+12, "ravager x6"),
        )
      ).asRight[String]
    )
  }

  private lazy val rawBuild =
    """
      |zvt allin 4.11.0
      |
      |13 0:16 overlord
      |16 0:41 spawning pool
      |18
      |1:13 hatchery
      |18 1:17 extractor
      |18    1:28 Queen
      |20 1:33 zergling x2
      |21 1:44 roach warren
      |
      |20 1:48 overlord
      |21 2:08 queen
      |
      |24 2:26 queen
      |24 2:28 roach x3
      |36 2:50          overlord
      |36 3:07 lair
      |44 3:26 overlord
      |44 3:30 Extractor
      |48 4:05 Extractor x2
      |48 4:07 glIal reconstitution
      |46
      |4:14
      |overseer
      |46 4:16 roach
      |49 4:21 hatchery
      |49 4:24 overlord
      |49 4:31 Roach
      |52 4:44 roach x4
      |60 4:59 roach
      |74 5:15 Evolution chamber x2
      |99 6:12 ravager x6
      |""".stripMargin
}
