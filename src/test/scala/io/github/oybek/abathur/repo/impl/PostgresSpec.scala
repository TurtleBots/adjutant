package io.github.oybek.abathur.repo.impl

import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.munit.TestContainerForAll
import io.github.oybek.abathur.Main
import io.github.oybek.abathur.config.Config.DB
import munit.FunSuite
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api.Database

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

trait PostgresSpec extends TestContainerForAll {
  this: FunSuite =>

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

  def createDbRunner(postgresContainer: PostgreSQLContainer): PostgresProfile.backend.DatabaseDef = {
    Database.forURL(
      url = postgresContainer.jdbcUrl,
      user = postgresContainer.username,
      driver = postgresContainer.driverClassName,
      password = postgresContainer.password
    )
  }
}
