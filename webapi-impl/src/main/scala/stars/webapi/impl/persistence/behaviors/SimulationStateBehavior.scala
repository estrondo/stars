package stars.webapi.impl.persistence.behaviors

import stars.webapi.impl.persistence.{SimulationCommand, SimulationEvent, SimulationState}
import stars.webapi.impl.persistence.SimulationState.RE

trait SimulationStateBehavior {

  def apply(command: SimulationCommand): RE

  def apply(event: SimulationEvent): SimulationState
}
