package io.github.oybek.adjutant.bot

import cats.data.NonEmptyList
import cats.effect.{Sync, Timer}
import cats.implicits._
import cats.{Parallel, ~>}
import enumeratum.EnumEntry
import io.github.oybek.adjutant.model.{Build, Command, Journal}
import io.github.oybek.adjutant.repo.JournalRepo
import io.github.oybek.adjutant.service.build.BuildService
import io.github.oybek.adjutant.service.qlang.{CommandParser, QueryLang}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import telegramium.bots.{Message, _}
import telegramium.bots.high.implicits._
import telegramium.bots.high.{Api, LongPollBot}

import java.sql.Timestamp
import scala.concurrent.duration.DurationInt

class Bot[F[_]: Sync: Parallel: Timer, DB[_]](implicit
                                              val api: Api[F],
                                              journalRepo: JournalRepo[DB],
                                              buildService: BuildService[F],
                                              dbRun: DB ~> F) extends LongPollBot[F](api) {

  override def onMessage(message: Message): F[Unit] = {
    implicit val messageImpl = message
    implicit val chatId: ChatIntId = ChatIntId(message.chat.id)
    Seq(
      message.text.map(whenText),
      message.voice.map(_ => whenVoice)
    ).reduce(_ orElse _).getOrElse(sendConfused)
  }

  private def whenVoice(implicit chatId: ChatIntId): F[Unit] =
    sendText("Reply to this voice message with command `/pin2buildX` to attach it to the build with id X")

  private def whenText(text: String)(implicit chatId: ChatIntId, message: Message): F[Unit] =
    Seq(
      CommandParser.parseStartOrHelp(text).map(_ =>
        for {
          buildCount <- buildService.getBuildCount
          _ <- whenGotHelpAsk(buildCount)
        } yield ()
      ),
      CommandParser.parseBuild(text).map { case (b, cs) => whenGotBuildOffer(b, cs) },
      CommandParser.parseBuildId(text).map(whenBuildAskedById),
      CommandParser.parseQuery(text).map(whenGotQuery),
      CommandParser.parseVoiceBuild(text).map(whenVoiceBuild)
    ).reduce(_ orElse _).getOrElse(sendConfused)

  private def whenVoiceBuild(buildId: Int)(implicit chatId: ChatIntId, message: Message): F[Unit] =
    message.replyToMessage.flatMap(_.voice).fold(
      sendText("You have to reply to voice message")
    ) { voice =>
      buildService.getBuildById(buildId).flatMap {
        case Some((build, _)) if build.author != chatId.id =>
          sendText("Only the author of the build can do it")
        case Some((build, _)) =>
          build.dictationTgId.fold(
            buildService.setDictationTgId(buildId, voice.fileId) >>
            sendText(s"Voice successfully pinned to /build$buildId")
          )(_ => sendText("Build already has the voice"))
        case None =>
          sendText(s"No build with id $buildId")
      }
    }

  private def whenGotHelpAsk(buildCount: Int = 0)(implicit chatId: ChatIntId) =
    sendText(
      s"""We have $buildCount builds in our database
         |
         |Every build has 3 properties:
         |matchup - it is `tvp`, `tvz`, `zvt`, ...
         |build type - `allin`, `economic`, `cheese` or `timingAttack`
         |duration - measured in minutes
         |
         |You can query builds using that properties:
         |
         |`tvp` - choose all tvp builds
         |
         |`tvp & economic` - choose all tvp builds that are also economic
         |
         |Or more complex queries
         |`(tvz & <6) | (tvz & !cheese & <10)` - which means choose all builds that are tvz and shorter than 6 minutes or that are tvz and not cheese builds and are shorter than 10 minutes
         |""".stripMargin)

  private def whenGotQuery(query: QueryLang.Expr)(implicit chatId: ChatIntId) =
    for {
      builds <- buildService.getBuildsByQuery(query)
      text = builds match {
        case Nil => "No builds with such configuration"
        case bs => drawBuilds(bs)
      }
      _ <- sendText(text)
    } yield ()

  private def whenBuildAskedById(buildId: Int)(implicit chatId: ChatIntId) =
    for {
      buildOpt <- buildService.getBuildById(buildId)
      _ <- buildOpt.traverse { case (build, _) =>
        dbRun {
          journalRepo.add(
            Journal(
              userId = chatId.id,
              buildId = build.id,
              timestamp = new Timestamp(System.currentTimeMillis())
            )
          )
        }
      }
      text = buildOpt.fold("No builds with such id") { case (b, cs) =>
        drawBuild(b, cs)
      }
      _ <- sendText(text)
      _ <- Timer[F].sleep(50.millis)
      _ <- buildOpt.flatMap(_._1.dictationTgId).traverse(sendVoice)
    } yield ()

  private def whenGotBuildOffer(build: Build, commands: NonEmptyList[Command])(implicit chatId: ChatIntId) =
    for {
      res <- buildService.addBuild(build.copy(author = chatId.id), commands).attempt
      _ <- res.fold(
        th => sendText(s"Error occured: ${th.getLocalizedMessage}"),
        buildId => sendText(s"Build research complete ✅\nPress /build$buildId to see")
      )
    } yield ()

  private def sendConfused(implicit chatId: ChatId): F[Unit] =
    sendText("Unacceptable command\nPress /help")

  private def sendText(text: String)(implicit chatId: ChatId): F[Unit] =
    sendMessage(
      chatId = chatId,
      text = text,
      parseMode = Markdown.some
    ).exec.void

  private def sendVoice(fileId: String)(implicit chatId: ChatId): F[Unit] =
    sendVoice(
      chatId = chatId,
      voice = InputLinkFile(fileId),
    ).exec.attempt.void

  private def drawBuild(build: Build, commands: Seq[Command]): String = {
    val supplyWidth = commands.map(_.supply.toString.length).maxOption.getOrElse(0)
    s"`${build.matchUp} ${build.ttype} ${build.patch}`\n\n" +
      commands.map { command =>
        val whenDoSeconds = (command.whenDo%60).toString
        s"`${" " * (supplyWidth - command.supply.toString.length)}${command.supply} | " +
        s"${command.whenDo/60}:${if (whenDoSeconds.length == 1) "0" else ""}$whenDoSeconds `" +
        s"${command.whatDo}"
      }.mkString("\n")
  }

  private def drawBuilds(builds: Seq[Build]): String =
    builds.map(build =>
      s"/build${build.id} $thumbUp`${build.thumbsUp}` $thumbDown`${build.thumbsDown}` | `${build.matchUp}` `${build.ttype}`"
    ).mkString("\n")

  private val thumbUp = "\uD83D\uDC4D"
  private val thumbDown = "\uD83D\uDC4E"
}
