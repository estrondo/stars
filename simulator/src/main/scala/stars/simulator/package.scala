package stars

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.ActorContext
import com.typesafe.config.Config

package object simulator {

  implicit class StarsActorContext(context: ActorContext[_]) extends StarsActorSystem(context.system)

  implicit class StarsActorSystem(system: ActorSystem[_]) {

    def getConfig(path: String): Config = system.settings.config.getConfig(path)

    def getConfig: Config = system.settings.config
  }
}
