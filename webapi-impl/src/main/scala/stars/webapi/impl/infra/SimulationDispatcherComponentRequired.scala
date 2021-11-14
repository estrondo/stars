package stars.webapi.impl.infra

import akka.actor.typed.ActorRef
import stars.simulation.protocol.SimulationCommand


trait SimulationDispatcherComponentRequired {

  def simulationDispatcher: ActorRef[SimulationCommand]
}
