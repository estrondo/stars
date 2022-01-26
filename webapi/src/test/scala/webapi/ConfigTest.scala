package webapi

import com.typesafe.config.{Config, ConfigFactory}

object ConfigTest {

  def application: Config = {
    val app = ConfigFactory
      .parseResources("application.conf")

    val test = ConfigFactory
      .parseResources("application-test.conf")

    test.withFallback(app)
  }
}
