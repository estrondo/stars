package stars.webapi.impl

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import com.typesafe.config.{Config, ConfigFactory}

class ActorSpecWithConfig(config: Config)
  extends ScalaTestWithActorTestKit(ConfigFactory.parseResources("application-test.conf").withFallback(config))
    with Spec
