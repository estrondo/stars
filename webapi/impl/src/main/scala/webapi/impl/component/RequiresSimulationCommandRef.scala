package webapi.impl.component

import akka.actor.typed.ActorRef
import simulator.protocol.SimulationCommand


trait RequiresSimulationCommandRef {

  def simulationCommandRef: ActorRef[SimulationCommand]
}
