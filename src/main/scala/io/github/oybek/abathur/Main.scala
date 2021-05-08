package io.github.oybek.abathur

import cats.effect.{ExitCode, IO, IOApp}
import io.github.oybek.abathur.bot.Bot
import io.github.oybek.abathur.config.Config
import io.github.oybek.abathur.deps.Deps
import io.github.oybek.abathur.service.impl.ParserServiceImpl
import org.typelevel.log4cats.slf4j.Slf4jLogger
import telegramium.bots.high.{Api, BotApi}

object Main extends Deps[IO] with IOApp {

  private val logger = Slf4jLogger.getLoggerFromClass[IO](Main.getClass)

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- logger.info("booting the application...")
      config <- readConfig
      _ <- logger.info(s"loaded configs: $config")
      _ <- migrate(config.db)
      _ <- resources(config).use {
        case (_, http) =>
          implicit val api: Api[IO] = BotApi(
            http,
            baseUrl = s"https://api.telegram.org/bot${config.tg.token}"
          )
          implicit val parserService = new ParserServiceImpl
          new Bot[IO].start()
      }
    } yield ExitCode.Success

  private def resources(config: Config) =
    for {
      db <- createDb(config.db)
      http <- createHttpClient
    } yield (db, http)
}