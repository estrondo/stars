package stars.webapi.impl.persistence

import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.EventSourcedBehavior

object SimulationEntityActor {

  def apply(persistenceId: PersistenceId): Behavior[SimulationCommand] = {
    EventSourcedBehavior.withEnforcedReplies[SimulationCommand, SimulationEvent, SimulationState](
      persistenceId,
      SimulationState.Empty,
      (state, command) => state(command),
      (state, event) => state(event)
    )
  }
}
