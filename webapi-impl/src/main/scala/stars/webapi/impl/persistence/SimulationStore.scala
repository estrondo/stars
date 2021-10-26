package stars.webapi.impl.persistence

import akka.actor.typed.Behavior
import akka.cluster.sharding.typed.scaladsl.{EntityContext, EntityTypeKey}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.EventSourcedBehavior

object SimulationStore {

  val empty: SimulationState = SimulationState.Empty

  val typeKey: EntityTypeKey[SimulationCommand] = EntityTypeKey[SimulationCommand]("simulation-store")

  def apply(ctx: EntityContext[SimulationCommand]): Behavior[SimulationCommand] = {
    EventSourcedBehavior.withEnforcedReplies[SimulationCommand, SimulationEvent, SimulationState](
      persistenceId = PersistenceId(ctx.entityTypeKey.name, ctx.entityId),
      emptyState = empty,
      commandHandler = (state, command) => state(command),
      eventHandler = (state, event) => state(event)
    )
  }

}
