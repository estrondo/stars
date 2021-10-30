package stars.webapi.impl.persistence.simulation

import stars.webapi.impl.persistence.{SimulationCommand, SimulationEvent, SimulationState}
import stars.webapi.impl.persistence.SimulationState.RE

trait WaitingBehavior extends SimulationStateBehavior {

  override def apply(command: SimulationCommand): RE = {
    throw new UnsupportedOperationException
  }

  override def apply(event: SimulationEvent): SimulationState = {
    throw new UnsupportedOperationException
  }
}
