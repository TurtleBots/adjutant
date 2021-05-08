package io.github.oybek.abathur.deps

import cats.effect.{IO, Resource, Sync}
import cats.implicits.catsSyntaxApplicativeId
import io.github.oybek.abathur.config.Config
import io.github.oybek.abathur.config.Config.DB
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderException
import pureconfig.generic.auto._
import slick.jdbc.PostgresProfile.api.Database
import slick.jdbc.PostgresProfile.backend.DatabaseDef

class Deps[F[_]: Sync] {
  def readConfig: IO[Config] =
    for {
      configReadResult <- IO(ConfigSource.default.load[Config])
      config <- configReadResult.fold(
        e => IO.raiseError[Config](new ConfigReaderException[Config](e)),
        c => c.pure[IO]
      )
    } yield config

  def createDb(db: DB): Resource[F, DatabaseDef] = {
    import db._
    Resource.make[F, DatabaseDef](
      Sync[F].delay(
        Database.forURL(
          url = url,
          user = user,
          driver = driver,
          password = password
        )
      )
    ) { databaseDef =>
      Sync[F].delay(databaseDef.close())
    }
  }

  def migrate(db: DB): IO[MigrateResult] =
    IO {
      Flyway
        .configure()
        .dataSource(
          db.url,
          db.user,
          db.password)
        .load()
        .migrate()
    }
}
