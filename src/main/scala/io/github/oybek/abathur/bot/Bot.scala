package io.github.oybek.abathur.bot

import cats.Parallel
import cats.effect.{Sync, Timer}
import cats.implicits._
import io.github.oybek.abathur.service.ParserService
import telegramium.bots.Message
import telegramium.bots._
import telegramium.bots.high.implicits._
import telegramium.bots.high.{Api, LongPollBot}

class Bot[F[_]: Sync: Parallel: Timer](implicit api: Api[F],
                                       parserService: ParserService) extends LongPollBot[F](api) {

  override def onMessage(message: Message): F[Unit] = {
    implicit val chatId: ChatId = ChatIntId(message.chat.id)
    Seq(
      message.text.map(whenText),
      (message.audio, message.caption).mapN(whenAudioWithCaption)
    ).reduce(_ orElse _).getOrElse(sendConfused)
  }

  private def whenText(text: String)(implicit chatId: ChatId): F[Unit] =
    Seq(
      parserService.parseBuild(text).map { case (build, commands) =>
        sendText(s"$build $commands")
      },
      parserService.parseBuildId(text).map { buildId =>
        sendText(s"$buildId")
      },
      parserService.parseQuery(text).map { queries =>
        sendText(s"$queries")
      }
    ).reduce(_ orElse _).getOrElse(sendConfused)

  private def whenAudioWithCaption(audio: Audio, caption: String)(implicit chatId: ChatId): F[Unit] =
    sendAudio(audio.fileId)

  private def sendConfused(implicit chatId: ChatId): F[Unit] =
    sendText("Не очень тебя понял братан")

  private def sendText(text: String)(implicit chatId: ChatId): F[Unit] =
    sendMessage(
      chatId = chatId,
      text = text,
    ).exec.void

  private def sendAudio(fileId: String)(implicit chatId: ChatId): F[Unit] =
    sendAudio(
      chatId = chatId,
      audio = InputLinkFile(fileId),
    ).exec.void
}
