package io.github.oybek.abathur.repo.impl

import io.github.oybek.abathur.model.Donors
import munit.FunSuite

class JournalRepoImplSpec extends FunSuite with PostgresSpec with Donors {
  test("JournalRepoImplSpec") {
    withContainers { postgresContainer =>
      val db = createDbRunner(postgresContainer)
      val journalRepoImpl = new JournalRepoImpl
      val buildRepoImpl = new BuildRepoImpl
      db.run(
        for {
          _ <- journalRepoImpl.add(journalDonor).failed

          generatedId <- buildRepoImpl.add(buildDonor)
          _ <- journalRepoImpl.add(journalDonor.copy(buildId = generatedId))
        } yield ()
      )
    }
  }
}
