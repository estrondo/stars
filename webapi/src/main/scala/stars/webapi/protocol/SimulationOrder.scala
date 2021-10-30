package stars.webapi.protocol

import java.util.UUID

object SimulationOrder {

  def apply(simulation: CreateSimulation): SimulationOrder = SimulationOrder(UUID.randomUUID(), simulation)
}

case class SimulationOrder(id: UUID, simulation: CreateSimulation)
