package webapi.impl.simulator

import akka.actor.typed.ActorRef
import simulator.protocol.Simulation
import webapi.impl.protocol.Attempt

object Command {

  case class Accepted(id: String) extends Command

  case class Create(simulation: Simulation, replyTo: ActorRef[CreateResponse]) extends Command

  case class CreateResponse(content: Attempt[Simulation, Simulation])
}

sealed trait Command
