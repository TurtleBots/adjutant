package io.github.oybek.abathur.config

import io.github.oybek.abathur.Main
import munit.FunSuite

class ConfigSpec extends FunSuite {

  test("configSpec") {
    Main.readConfig.map { config =>
      assertEquals(config, config)
    }.unsafeRunSync()
  }
}
