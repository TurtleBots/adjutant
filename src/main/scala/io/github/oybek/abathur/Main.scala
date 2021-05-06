package io.github.oybek.abathur

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.catsSyntaxApplicativeId
import io.github.oybek.abathur.config.Config
import io.github.oybek.abathur.deps.Deps
import org.flywaydb.core.Flyway
import org.typelevel.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderException
import pureconfig.generic.auto._

import java.sql.Time

object Main extends Deps[IO] with IOApp {

  private val logger = Slf4jLogger.getLoggerFromClass[IO](Main.getClass)

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- logger.info("booting the application...")
      configReadResult <- IO(ConfigSource.default.load[Config])
      config <- configReadResult.fold(
        e => IO.raiseError[Config](new ConfigReaderException[Config](e)),
        c => c.pure[IO]
      )
      _ <- logger.info(s"loaded configs: $config")
      _ <- IO {
        Flyway
          .configure()
          .dataSource(
            config.db.url,
            config.db.user,
            config.db.password)
          .load()
          .migrate()
      }
      _ <- createDb(config.db).use {
        db =>
          ().pure[IO]
      }
    } yield ExitCode.Success
}