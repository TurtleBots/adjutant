import sbt._

object Dependencies {
  object V {
    val catsCore = "2.6.0"
    val catsEffect = "3.1.0"
    val enumeratum = "1.6.1"
    val enumeratumSlick = "1.6.0"
    val flyway = "7.5.4"
    val jdbcPostgres = "42.2.20"
    val log4cats = "2.1.0"
    val logback = "1.2.3"
    val pureConfig = "0.15.0"
    val slick = "3.3.3"

    val munit = "0.7.25"
    val randomDataGenerator = "2.9"
    val testContainers = "0.39.3"
  }

  val common: Seq[ModuleID] = Seq(
    "ch.qos.logback"        %  "logback-classic"       % V.logback,
    "com.beachape"          %% "enumeratum"            % V.enumeratum,
    "com.beachape"          %% "enumeratum-slick"      % V.enumeratumSlick,
    "com.github.pureconfig" %% "pureconfig"            % V.pureConfig,
    "com.typesafe.slick"    %% "slick"                 % V.slick,
    "com.typesafe.slick"    %% "slick-hikaricp"        % V.slick,
    "org.flywaydb"          %  "flyway-core"           % V.flyway,
    "org.postgresql"        %  "postgresql"            % V.jdbcPostgres,
    "org.typelevel"         %% "cats-core"             % V.catsCore,
    "org.typelevel"         %% "cats-effect"           % V.catsEffect,
    "org.typelevel"         %% "log4cats-core"         % V.log4cats,
    "org.typelevel"         %% "log4cats-slf4j"        % V.log4cats,

    "com.danielasfregola"   %% "random-data-generator"           % V.randomDataGenerator % Test,
    "com.dimafeng"          %% "testcontainers-scala-munit"      % V.testContainers % Test,
    "com.dimafeng"          %% "testcontainers-scala-postgresql" % V.testContainers % Test,
    "org.scalameta"         %% "munit"                           % V.munit % Test,
  )

}