package webapi.impl.component

import akka.actor.typed.ActorRef
import simulation.protocol.SimulationCommand


trait RequiresSimulationCommandDispatcher {

  def simulationCommandDispatcher: ActorRef[SimulationCommand]
}
