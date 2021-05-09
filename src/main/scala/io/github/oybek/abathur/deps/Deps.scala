package io.github.oybek.abathur.deps

import cats.Monad
import cats.effect.{IO, IOApp, Resource, Sync}
import cats.implicits.catsSyntaxApplicativeId
import io.github.oybek.abathur.config.Config
import io.github.oybek.abathur.config.Config.DB
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderException
import pureconfig.generic.auto._
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile.api.Database
import slick.jdbc.PostgresProfile.backend.DatabaseDef

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

class Deps[F[_]: Sync] {
  this: IOApp =>

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

  def createHttpClient: Resource[IO, Client[IO]] = {
    val executionContext: ExecutionContext =
      ExecutionContext.fromExecutor(Executors.newCachedThreadPool)
    BlazeClientBuilder[IO](executionContext).resource
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

  def dbioMonad(implicit executionContext: ExecutionContext): Monad[DBIO] =
    new Monad[DBIO] {
      override def flatMap[A, B](fa: DBIO[A])(f: A => DBIO[B]): DBIO[B] = fa.flatMap(f)

      override def pure[A](x: A): DBIO[A] = DBIO.successful(x)

      // TODO: do it tailrec
      override def tailRecM[A, B](a: A)(f: A => DBIO[Either[A, B]]): DBIO[B] = {
        f(a).flatMap {
          case Left(nextA) => tailRecM(nextA)(f)
          case Right(b) => DBIO.successful(b)
        }
      }
    }
}
