package io.github.oybek.abathur

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.catsSyntaxApplicativeId
import io.github.oybek.abathur.config.Config
import io.github.oybek.abathur.config.Config.DB
import io.github.oybek.abathur.deps.Deps
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult
import org.typelevel.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderException
import pureconfig.generic.auto._

object Main extends Deps[IO] with IOApp {

  private val logger = Slf4jLogger.getLoggerFromClass[IO](Main.getClass)

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- logger.info("booting the application...")
      config <- readConfig
      _ <- logger.info(s"loaded configs: $config")
      _ <- migrate(config.db)
      _ <- createDb(config.db).use {
        db =>
          ().pure[IO]
      }
    } yield ExitCode.Success

  private def readConfig: IO[Config] =
    for {
      configReadResult <- IO(ConfigSource.default.load[Config])
      config <- configReadResult.fold(
        e => IO.raiseError[Config](new ConfigReaderException[Config](e)),
        c => c.pure[IO]
      )
    } yield config

  def migrate(db: DB): IO[MigrateResult] =
    IO {
      Flyway
        .configure()
        .dataSource(
          db.url,
          db.user,
          db.password)
        .load()
        .migrate()
    }
}