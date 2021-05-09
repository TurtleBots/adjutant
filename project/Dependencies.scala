import sbt._

object Dependencies {
  object V {
    val atto = "0.7.0"
    val catsCore = "2.6.0"
    val catsEffect = "2.5.0"
    val enumeratum = "1.6.1"
    val enumeratumSlick = "1.6.0"
    val flyway = "7.5.4"
    val http4s = "0.21.22"
    val jdbcPostgres = "42.2.20"
    val log4cats = "1.2.2"
    val logback = "1.2.3"
    val pureConfig = "0.15.0"
    val slick = "3.3.3"
    val telegramium = "4.52.0"

    val munit = "0.7.25"
    val randomDataGenerator = "2.9"
    val scalaMock = "5.1.0"
    val scalaTest = "3.1.0"
    val testContainers = "0.39.3"
  }

  val common: Seq[ModuleID] = Seq(
    "ch.qos.logback"        %  "logback-classic"     % V.logback,
    "com.beachape"          %% "enumeratum"          % V.enumeratum,
    "com.beachape"          %% "enumeratum-slick"    % V.enumeratumSlick,
    "com.github.pureconfig" %% "pureconfig"          % V.pureConfig,
    "com.typesafe.slick"    %% "slick"               % V.slick,
    "com.typesafe.slick"    %% "slick-hikaricp"      % V.slick,
    "io.github.apimorphism" %% "telegramium-core"    % V.telegramium,
    "io.github.apimorphism" %% "telegramium-high"    % V.telegramium,
    "org.flywaydb"          %  "flyway-core"         % V.flyway,
    "org.http4s"            %% "http4s-blaze-client" % V.http4s,
    "org.http4s"            %% "http4s-circe"        % V.http4s,
    "org.postgresql"        %  "postgresql"          % V.jdbcPostgres,
    "org.tpolecat"          %% "atto-core"           % V.atto,
    "org.tpolecat"          %% "atto-refined"        % V.atto,
    "org.typelevel"         %% "cats-core"           % V.catsCore,
    "org.typelevel"         %% "cats-effect"         % V.catsEffect,
    "org.typelevel"         %% "log4cats-core"       % V.log4cats,
    "org.typelevel"         %% "log4cats-slf4j"      % V.log4cats,

    "com.danielasfregola"   %% "random-data-generator"           % V.randomDataGenerator % Test,
    "com.dimafeng"          %% "testcontainers-scala-munit"      % V.testContainers % Test,
    "com.dimafeng"          %% "testcontainers-scala-postgresql" % V.testContainers % Test,
    "org.scalameta"         %% "munit"                           % V.munit % Test,
    "org.scalamock"         %% "scalamock"                       % V.scalaMock % Test,
    "org.scalatest"         %% "scalatest"                       % V.scalaTest % Test
  )
}
