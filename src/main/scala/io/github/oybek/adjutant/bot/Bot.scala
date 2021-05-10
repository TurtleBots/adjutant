package io.github.oybek.adjutant.bot

import cats.data.NonEmptyList
import cats.effect.{Sync, Timer}
import cats.implicits._
import cats.{Parallel, ~>}
import enumeratum.EnumEntry
import io.github.oybek.adjutant.model.{Build, Command, Journal}
import io.github.oybek.adjutant.repo.JournalRepo
import io.github.oybek.adjutant.service.{BuildService, ParserService}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import telegramium.bots.{Message, _}
import telegramium.bots.high.implicits._
import telegramium.bots.high.{Api, LongPollBot}

import java.sql.Timestamp
import scala.concurrent.duration.DurationInt

class Bot[F[_]: Sync: Parallel: Timer, DB[_]](implicit
                                              val api: Api[F],
                                              parserService: ParserService,
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
      parserService.parseAll(text).map(_ => whenGotAll),
      parserService.parseStartOrHelp(text).map(_ => whenGotHelpAsk),
      parserService.parseBuild(text).map { case (b, cs) => whenGotBuildOffer(b, cs) },
      parserService.parseBuildId(text).map(whenBuildAskedById),
      parserService.parseQuery(text).map(whenGotQuery),
      parserService.parseVoiceBuild(text).map(whenVoiceBuild)
    ).reduce(_ orElse _).getOrElse(sendConfused)

  private def whenVoiceBuild(buildId: Int)(implicit chatId: ChatIntId, message: Message): F[Unit] =
    message.replyToMessage.flatMap(_.voice).fold(
      sendText("You have to reply to voice message")
    ) { voice =>
      buildService.getBuild(buildId).flatMap {
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

  private def whenGotHelpAsk(implicit chatId: ChatIntId) =
    sendText(
      """Search query examples:
        |`tvp economic`
        |`zvt`
        |`zvt allin roach`
        |""".stripMargin)

  private def whenGotAll(implicit chatId: ChatIntId) =
    for {
      builds <- buildService.getBuilds(Seq.empty[EnumEntry])
      text = builds match {
        case Nil => "No builds in the system"
        case bs => drawBuilds(bs)
      }
      _ <- sendText(text)
    } yield ()

  private def whenGotQuery(query: NonEmptyList[EnumEntry])(implicit chatId: ChatIntId) =
    for {
      builds <- buildService.getBuilds(query.toList)
      text = builds match {
        case Nil => "No builds with such configuration"
        case bs => drawBuilds(bs)
      }
      _ <- sendText(text)
    } yield ()

  private def whenBuildAskedById(buildId: Int)(implicit chatId: ChatIntId) =
    for {
      buildOpt <- buildService.getBuild(buildId)
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
