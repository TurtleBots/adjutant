package io.github.oybek.adjutant.config

import io.github.oybek.adjutant.Main
import munit.FunSuite

class ConfigSpec extends FunSuite {

  test("configSpec") {
    Main.readConfig.map { config =>
      assertEquals(config, config)
    }.unsafeRunSync()
  }
}
