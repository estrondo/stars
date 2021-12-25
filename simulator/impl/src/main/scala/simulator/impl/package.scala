import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.ActorContext
import com.typesafe.config.Config

package object simulator {

  implicit class StarsActorContext(context: ActorContext[_]) extends StarsActorSystem(context.system)

  implicit class StarsActorSystem(system: ActorSystem[_]) {

    def config(path: String): Config = system.settings.config.getConfig(path)

    def config: Config = system.settings.config
  }
}
