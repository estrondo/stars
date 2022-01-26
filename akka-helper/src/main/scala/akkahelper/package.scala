import akka.actor.typed.{ActorRef, ActorSystem}
import akka.cluster.sharding.typed.ShardingEnvelope
import com.typesafe.config.Config

package object akkahelper {

  type ShardActorRef[M] = ActorRef[ShardingEnvelope[M]]

  implicit class ActorSystemWithConfig(system: ActorSystem[_]) {

    def config: Config = system.settings.config

  }
}
