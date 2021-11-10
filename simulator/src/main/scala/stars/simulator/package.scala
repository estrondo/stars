package stars

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.ActorContext
import com.typesafe.config.Config

package object simulator {

  implicit class ActorContextOps(context: ActorContext[_]) extends ActorSystemOps(context.system)

  implicit class ActorSystemOps(system: ActorSystem[_]) {

    def getConfig(path: String): Config = system.settings.config.getConfig(path)

    def getConfig: Config = system.settings.config
  }
}
