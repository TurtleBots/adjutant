package io.github.oybek.adjutant

import cats.effect.{ExitCode, IO, IOApp}
import cats.{Monad, ~>}
import io.github.oybek.adjutant.bot.Bot
import io.github.oybek.adjutant.config.Config
import io.github.oybek.adjutant.deps.Deps
import io.github.oybek.adjutant.repo.impl.{BuildRepoImpl, CommandRepoImpl}
import io.github.oybek.adjutant.service.impl.{BuildServiceImpl, ParserServiceImpl}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import slick.dbio.DBIO
import telegramium.bots.high.{Api, BotApi}

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object Main extends Deps[IO] with IOApp {

  private val logger = Slf4jLogger.getLoggerFromClass[IO](Main.getClass)

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- logger.info("booting the application...")
      config <- readConfig
      _ <- logger.info(s"loaded configs: $config")
      _ <- migrate(config.db)
      _ <- resources(config).use {
        case (dbRunner, http) =>
          implicit val executionContext: ExecutionContext = {
            ExecutionContext.fromExecutor(
              Executors.newFixedThreadPool(10)
            )
          }
          implicit val dbioMonad: Monad[DBIO] = Main.dbioMonad(executionContext)
          implicit val dbRun: DBIO ~> IO = new ~>[DBIO, IO] {
            override def apply[A](fa: DBIO[A]): IO[A] =
              IO.fromFuture(IO(dbRunner.run(fa)))
          }
          implicit val api: Api[IO] = BotApi(
            http,
            baseUrl = s"https://api.telegram.org/bot${config.tg.token}"
          )
          implicit val parserService = new ParserServiceImpl
          implicit val buildRepo = new BuildRepoImpl
          implicit val commandRepo = new CommandRepoImpl
          implicit val buildServiceImpl = new BuildServiceImpl[IO, DBIO]
          new Bot[IO].start()
      }
    } yield ExitCode.Success

  private def resources(config: Config) =
    for {
      db <- createDb(config.db)
      http <- createHttpClient
    } yield (db, http)
}