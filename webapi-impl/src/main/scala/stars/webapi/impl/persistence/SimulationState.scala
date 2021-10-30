package stars.webapi.impl.persistence

import akka.persistence.typed.scaladsl.ReplyEffect
import stars.webapi.impl.persistence.simulation.{EmptyBehavior, SimulationStateBehavior, WaitingBehavior}
import stars.webapi.protocol.SimulationOrder

object SimulationState {

  case object Empty extends SimulationState with EmptyBehavior

  type RE = ReplyEffect[SimulationEvent, SimulationState]

  case class Waiting(order: SimulationOrder) extends SimulationState with WaitingBehavior
}

sealed trait SimulationState extends SimulationStateBehavior