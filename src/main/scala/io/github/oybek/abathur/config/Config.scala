package io.github.oybek.abathur.config

import io.github.oybek.abathur.config.Config.{DB, Tg}

case class Config(db: DB, tg: Tg)

object Config {
  case class DB(driver: String,
                url: String,
                user: String,
                password: String)

  case class Tg(token: String)
}
