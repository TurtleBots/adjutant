import sbt._

object Dependencies {
  object V {
    val flyway = "7.5.4"
  }

  val flyway = Seq(
    "org.flywaydb" % "flyway-core" % V.flyway
  )

  val testContainers = Seq(
    "com.dimafeng" %% "testcontainers-scala-core" % "0.39.0" % "test",
    "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.39.0" % "test",
    "com.dimafeng" %% "testcontainers-scala-postgresql" % "0.39.0" % "test"
  )

  val common: Seq[ModuleID] =
    flyway ++ testContainers
}