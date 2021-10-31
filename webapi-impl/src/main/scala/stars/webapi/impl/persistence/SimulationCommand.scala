package stars.webapi.impl.persistence

import akka.actor.typed.ActorRef
import stars.webapi.protocol.SimulationOrder

object SimulationCommand {

  type Creation = Either[(SimulationOrder, Throwable), SimulationOrder]

  case class Create(order: SimulationOrder, replyTo: ActorRef[Creation]) extends SimulationCommand
}

sealed trait SimulationCommand
