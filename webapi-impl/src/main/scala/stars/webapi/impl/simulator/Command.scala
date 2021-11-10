package stars.webapi.impl.simulator

import akka.actor.typed.ActorRef
import stars.simulation.protocol.{NewSimulation, Simulation}

object Command {

  case class New(command: NewSimulation, replyTo: ActorRef[NewResponse]) extends Command

  case class NewResponse(simulation: Simulation, error: Option[Exception] = None)
}

sealed trait Command
