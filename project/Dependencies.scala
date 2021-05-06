import sbt._

object Dependencies {
  object V {
    val catsEffect = "2.1.4"
    val flyway = "7.5.4"
    val pureConfig = "0.15.0"
    val slick = "3.3.3"
    val testContainers = "0.39.0"
    val log4cats = "2.1.0"
  }

  val cats = Seq(
    "org.typelevel" %% "cats-effect" % V.catsEffect,
    "org.typelevel" %% "log4cats-core" % V.log4cats,
    "org.typelevel" %% "log4cats-slf4j" % V.log4cats
  )

  val flyway = Seq(
    "org.flywaydb" % "flyway-core" % V.flyway
  )

  val pureConfig = Seq(
    "com.github.pureconfig" %% "pureconfig" % V.pureConfig
  )

  val slick = Seq(
    "com.typesafe.slick" %% "slick" % V.slick,
    "com.typesafe.slick" %% "slick-hikaricp" % V.slick
  )

  val testContainers = Seq(
    "com.dimafeng" %% "testcontainers-scala-core" % V.testContainers % "test",
    "com.dimafeng" %% "testcontainers-scala-postgresql" % V.testContainers % "test",
    "com.dimafeng" %% "testcontainers-scala-scalatest" % V.testContainers % "test"
  )

  val logback = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )

  // https://mvnrepository.com/artifact/org.postgresql/postgresql
  val postgresJdbc = Seq(
    "org.postgresql" % "postgresql" % "42.2.20"
  )

  val common: Seq[ModuleID] =
    cats ++
    flyway ++
    logback ++
    slick ++
    postgresJdbc ++
    pureConfig ++
    testContainers
}