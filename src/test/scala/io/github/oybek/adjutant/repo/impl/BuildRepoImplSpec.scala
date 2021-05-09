package io.github.oybek.adjutant.repo.impl

import io.github.oybek.adjutant.model.{Build, BuildType, Donors}
import munit.FunSuite
import slick.jdbc.PostgresProfile.api.Database

class BuildRepoImplSpec extends FunSuite with PostgresSpec with Donors {
  test("BuildRepoImplSpec ") {
    withContainers { postgresContainer =>
      val db = createDbRunner(postgresContainer)
      val buildRepoImpl = new BuildRepoImpl
      db.run(
        for {
          generatedId <- buildRepoImpl.add(buildDonor)
          _ = assertEquals(generatedId, 1)

          build <- buildRepoImpl.get(generatedId)
          _ = assertEquals(build, Some(buildDonor.copy(id = generatedId)))

          builds <- buildRepoImpl.get(Some(buildDonor.matchUp), None, None)
          _ = assertEquals(builds, Seq(buildDonor.copy(id = generatedId)))

          builds <- buildRepoImpl.get(Some(buildDonor.matchUp), BuildType.values.find(_ != buildDonor.ttype), None)
          _ = assertEquals(builds, Seq.empty[Build])

          _ <- buildRepoImpl.thumbUp(generatedId)
          _ <- buildRepoImpl.thumbDown(generatedId)
          _ <- buildRepoImpl.setDictationTgId(generatedId, "123")
          build <- buildRepoImpl.get(generatedId)
          _ = assertEquals(
            build,
            Some(
              buildDonor.copy(
                id = generatedId,
                thumbsUp = buildDonor.thumbsUp + 1,
                thumbsDown = buildDonor.thumbsDown + 1,
                dictationTgId = Some("123")
              )
            )
          )
        } yield ()
      )
    }
  }
}
