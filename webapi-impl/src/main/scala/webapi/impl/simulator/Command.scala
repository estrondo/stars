package webapi.impl.simulator

import akka.actor.typed.ActorRef
import simulation.protocol.Simulation

object Command {

  case class Create(simulation: Simulation, replyTo: ActorRef[CreateResponse]) extends Command

  case class CreateResponse(simulation: Simulation, error: Option[Exception] = None)
}

sealed trait Command
