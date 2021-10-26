package stars.webapi.protocol

import java.util.UUID

object SimulationOrderID {

  def apply(order: SimulationOrder): SimulationOrderID = SimulationOrderID(UUID.randomUUID(), order)
}

case class SimulationOrderID(id: UUID, order: SimulationOrder)
