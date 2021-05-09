package io.github.oybek.adjutant.model

import cats.data.NonEmptyList
import com.danielasfregola.randomdatagenerator.RandomDataGenerator._
import org.scalacheck.Arbitrary
import org.scalacheck.Gen.chooseNum

import java.sql.Timestamp

trait Donors {
  val buildDonor = random[Build]
  val journalDonor = random[Journal]
  val commandsDonor = NonEmptyList.of(random[Command], random[Command](10): _*)

  implicit lazy val arbitraryTimestamp: Arbitrary[Timestamp] = Arbitrary(
    chooseNum(
      Timestamp.valueOf("2020-01-01 00:00:00").getTime,
      Timestamp.valueOf("2030-01-01 00:00:00").getTime
    ).map(new Timestamp(_))
  )
}
