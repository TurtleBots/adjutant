package io.github.oybek.abathur.model

import com.danielasfregola.randomdatagenerator.RandomDataGenerator._

trait Donors {
  val buildDonor = random[Build]
}
