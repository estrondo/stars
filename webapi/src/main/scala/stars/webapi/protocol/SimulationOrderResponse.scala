package stars.webapi.protocol

import java.util.UUID

case class SimulationOrderResponse(
  id: UUID,
  order: SimulationOrder
)
