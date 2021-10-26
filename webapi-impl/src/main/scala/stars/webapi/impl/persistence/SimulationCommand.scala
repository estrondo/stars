package stars.webapi.impl.persistence

import akka.actor.typed.ActorRef
import stars.webapi.protocol.SimulationOrderID

object SimulationCommand {

  type Creation = Either[(SimulationOrderID, Throwable), SimulationOrderID]

  case class Create(order: SimulationOrderID, replyTo: ActorRef[Creation]) extends SimulationCommand
}

sealed trait SimulationCommand
