package io.github.oybek.abathur

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.catsSyntaxApplicativeId
import io.github.oybek.abathur.deps.Deps
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends Deps[IO] with IOApp {

  private val logger = Slf4jLogger.getLoggerFromClass[IO](Main.getClass)

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- logger.info("booting the application...")
      config <- readConfig
      _ <- logger.info(s"loaded configs: $config")
      _ <- migrate(config.db)
      _ <- createDb(config.db).use {
        _ => ().pure[IO]
      }
    } yield ExitCode.Success
}