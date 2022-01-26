package webapi.simulator

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.freespec.AsyncFreeSpecLike
import org.scalatest.matchers.should.Matchers

abstract class SpecWithActorSystem(config: Config)
    extends ScalaTestWithActorTestKit(config)
    with AsyncFreeSpecLike
    with Matchers {

  def this() = this(ConfigFactory.load("application-test.conf"))
}
