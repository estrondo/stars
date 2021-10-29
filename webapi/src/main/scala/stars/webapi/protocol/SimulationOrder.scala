package stars.webapi.protocol

import java.util.UUID

object SimulationOrder {

  def apply(command: CreateSimulation): SimulationOrder = SimulationOrder(UUID.randomUUID(), command)
}

case class SimulationOrder(id: UUID, command: CreateSimulation)
