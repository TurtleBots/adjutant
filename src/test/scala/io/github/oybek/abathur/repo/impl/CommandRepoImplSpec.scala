package io.github.oybek.abathur.repo.impl

import io.github.oybek.abathur.model.BuildType.Allin
import io.github.oybek.abathur.model.MatchUp.ZvT
import io.github.oybek.abathur.model.UnitType.{Ravager, Roach, Ultralisk}
import io.github.oybek.abathur.model.{Build, Command, Donors}
import munit.FunSuite
import slick.jdbc.PostgresProfile.api.Database

class CommandRepoImplSpec extends FunSuite with PostgresSpec with Donors {
  test("CommandRepoImplSpec") {
    withContainers { postgresContainer =>
      val db = createDbRunner(postgresContainer)
      val buildRepo = new BuildRepoImpl
      val commandRepo = new CommandRepoImpl
      db.run(
        for {
          _ <- commandRepo.add(commands).failed

          generatedBuildId <- buildRepo.add(build)
          commandsWithActualId = commands.map(_.copy(buildId = generatedBuildId))
          _ <- commandRepo.add(commandsWithActualId)

          gotCommands <- commandRepo.get(build.id)
          _ = assertEquals(gotCommands, commandsWithActualId)

          buildIds <- commandRepo.getBuildIds(Roach)
          _ = assertEquals(buildIds, Seq(1))

          buildIds <- commandRepo.getBuildIds(Ravager)
          _ = assertEquals(buildIds, Seq(1))

          buildIds <- commandRepo.getBuildIds(Ultralisk)
          _ = assertEquals(buildIds, Seq.empty[Int])
        } yield ()
      )
    }
  }

  private val build = Build(
    1,
    ZvT,
    6*60+12,
    Allin,
    "4.11.0",
    "Railgan",
    0,
    0,
    Option.empty[String])

  private val commands = Seq(
    Command(13, 0*60+16, "Overlord"),
    Command(16, 0*60+41, "Spawning Pool"),
    Command(18, 1*60+13, "Hatchery"),
    Command(18, 1*60+17, "Extractor"),
    Command(18, 1*60+28, "Queen"),
    Command(20, 1*60+33, "Zergling x2"),
    Command(21, 1*60+44, "Roach Warren"),
    Command(20, 1*60+48, "Overlord"),
    Command(21, 2*60+ 8, "Queen"),
    Command(24, 2*60+26, "Queen"),
    Command(24, 2*60+28, "Roach x3"),
    Command(36, 2*60+50, "Overlord"),
    Command(36, 3*60+ 7, "Lair"),
    Command(44, 3*60+26, "Overlord"),
    Command(44, 3*60+30, "Extractor"),
    Command(48, 4*60+ 5, "Extractor x2"),
    Command(48, 4*60+ 7, "Glial Reconstitution"),
    Command(46, 4*60+14, "Overseer"),
    Command(46, 4*60+16, "Roach"),
    Command(49, 4*60+21, "Hatchery"),
    Command(49, 4*60+24, "Overlord"),
    Command(49, 4*60+31, "Roach"),
    Command(52, 4*60+44, "Roach x4"),
    Command(60, 4*60+59, "Roach"),
    Command(74, 5*60+15, "Evolution Chamber x2"),
    Command(99, 6*60+12, "Ravager x6")
  )
}
