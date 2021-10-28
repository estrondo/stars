package stars.webapi.protocol

import java.time.Instant
import java.util.UUID

case class SimulationOrderResponse(
  id: UUID,
  issuedAt: Instant,
  createdAt: Instant,
  order: SimulationOrder
)
