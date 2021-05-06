package io.github.oybek.abathur.deps

import cats.effect.{Resource, Sync}
import io.github.oybek.abathur.config.Config.DB
import slick.jdbc.PostgresProfile.api.Database
import slick.jdbc.PostgresProfile.backend.DatabaseDef

class Deps[F[_]: Sync] {

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
}
