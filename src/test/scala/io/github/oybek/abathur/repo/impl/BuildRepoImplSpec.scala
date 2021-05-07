package io.github.oybek.abathur.repo.impl

import cats.effect.unsafe.implicits.global
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.munit.TestContainerForAll
import io.github.oybek.abathur.Main
import io.github.oybek.abathur.config.Config.DB
import io.github.oybek.abathur.model.BuildType.Cheese
import io.github.oybek.abathur.model.{Build, Donors}
import munit.FunSuite
import slick.jdbc.PostgresProfile.api.Database

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

class BuildRepoImplSpec extends FunSuite with TestContainerForAll with Donors {
  override val containerDef: PostgreSQLContainer.Def = PostgreSQLContainer.Def()
  implicit val executionContext: ExecutionContext =
    ExecutionContext.fromExecutor(
      Executors.newFixedThreadPool(10)
    )

  override def afterContainersStart(container: containerDef.Container): Unit = {
    Main.migrate(
      DB(
        url = container.jdbcUrl,
        driver = container.driverClassName,
        user = container.username,
        password = container.password
      )
    ).unsafeRunSync()
  }

  test("create and retrieve the build") {
    withContainers { postgresContainer =>
      val db =
        Database.forURL(
          url = postgresContainer.jdbcUrl,
          user = postgresContainer.username,
          driver = postgresContainer.driverClassName,
          password = postgresContainer.password
        )
      val buildRepoImpl = new BuildRepoImpl
      db.run(
        for {
          generatedId <- buildRepoImpl.add(buildDonor)
          _ = assertEquals(generatedId, 1)

          build <- buildRepoImpl.get(generatedId)
          _ = assertEquals(build, Some(buildDonor.copy(id = generatedId)))

          builds <- buildRepoImpl.get(Some(buildDonor.matchUp), None, None)
          _ = assertEquals(builds, Seq(buildDonor.copy(id = generatedId)))

          builds <- buildRepoImpl.get(Some(buildDonor.matchUp), Some(Cheese), None)
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
