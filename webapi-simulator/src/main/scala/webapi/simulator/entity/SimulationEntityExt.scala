package webapi.simulator.entity

import akka.actor.typed.{ActorSystem, Behavior, Extension, ExtensionId}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.EventSourcedBehavior
import akkahelper.ShardActorRef
import webapi.simulator.entity.SimulationEntity.{Command, Event, State, TypeKey}

object SimulationEntityExt extends ExtensionId[SimulationEntityExt] {

  def createBehavior(id: String): Behavior[Command] = {
    val entity = new SimulationEntity(id)
    EventSourcedBehavior
      .apply[Command, Event, State](
        persistenceId = PersistenceId(SimulationEntity.TypeKey.name, id),
        emptyState = SimulationEntity.EmptyState,
        commandHandler = entity.commandHandler,
        eventHandler = entity.eventHandler
      )
      .withTagger(entity.tagger)
  }

  override def createExtension(system: ActorSystem[_]): SimulationEntityExt = {
    new SimulationEntityExt(system)
  }
}

class SimulationEntityExt(system: ActorSystem[_]) extends Extension {

  val sharding: ShardActorRef[Command] =
    ClusterSharding(system).init(Entity(TypeKey)(ctx => SimulationEntityExt.createBehavior(ctx.entityId)))

}
