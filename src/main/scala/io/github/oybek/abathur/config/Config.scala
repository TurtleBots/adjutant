package io.github.oybek.abathur.config

import io.github.oybek.abathur.config.Config.DB

case class Config(db: DB)

object Config {
  case class DB(driver: String,
                url: String,
                user: String,
                password: String)
}
