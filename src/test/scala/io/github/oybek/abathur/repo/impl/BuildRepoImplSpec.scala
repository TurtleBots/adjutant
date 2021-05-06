package io.github.oybek.abathur.repo.impl

import cats.effect.unsafe.implicits.global
import com.dimafeng.testcontainers.{ContainerDef, PostgreSQLContainer}
import com.dimafeng.testcontainers.munit.TestContainerForAll
import io.github.oybek.abathur.Main
import io.github.oybek.abathur.config.Config.DB
import io.github.oybek.abathur.model.Donors
import munit.FunSuite
import slick.jdbc.PostgresProfile.api.Database

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

class BuildRepoImplSpec extends FunSuite with TestContainerForAll with Donors {
  override val containerDef: ContainerDef = PostgreSQLContainer.Def()
  implicit val executionContext: ExecutionContext =
    ExecutionContext.fromExecutor(
      Executors.newFixedThreadPool(10)
    )

  override def afterContainersStart(container: containerDef.Container): Unit = {
    val postgresContainer = container.asInstanceOf[PostgreSQLContainer]
    Main.migrate(
      DB(
        url = postgresContainer.jdbcUrl,
        driver = postgresContainer.driverClassName,
        user = postgresContainer.username,
        password = postgresContainer.password
      )
    ).unsafeRunSync()
  }

  test("create and retrieve the build") {
    withContainers { container =>
      val postgresContainer = container.asInstanceOf[PostgreSQLContainer]
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

          build <- buildRepoImpl.get(buildDonor.matchUp)
          _ = assertEquals(build, Seq(buildDonor.copy(id = generatedId)))

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
