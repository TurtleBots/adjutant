package io.github.oybek.abathur.service

import cats.data.NonEmptyList
import enumeratum.EnumEntry
import io.github.oybek.abathur.model.{Build, Command}

trait ParserService {
  def parseBuildId(text: String): Either[String, Int]
  def parseQuery(text: String): Either[String, NonEmptyList[EnumEntry]]
  def parseBuild(text: String): Either[String, (Build, NonEmptyList[Command])]
  def parseStartOrHelp(text: String): Either[String, String]
}
