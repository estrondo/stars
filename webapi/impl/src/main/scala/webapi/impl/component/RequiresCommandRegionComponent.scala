package webapi.impl.component

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity}
import com.lightbend.lagom.scaladsl.server.LagomServerComponents
import webapi.impl.simulator.{Command, SimulatorEntity}

trait RequiresCommandRegionComponent {

  def commandRegion: ActorRef[ShardingEnvelope[Command]]
}


trait CommandRegionComponent extends RequiresCommandRegionComponent {
  this: LagomServerComponents
    with RequiresSimulationCommandRef =>

  override def commandRegion: ActorRef[ShardingEnvelope[Command]] = {
    ClusterSharding(ActorSystem.wrap(actorSystem)).init(Entity(SimulatorEntity.TypeKey)({ context =>
      SimulatorEntity.create(context, simulationCommandRef)
    }))
  }
}