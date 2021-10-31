package stars.webapi.protocol

import java.time.Instant
import java.util.UUID

case class CreateSimulationResponse(
  id: UUID,
  createdAt: Instant,
  order: CreateSimulation
)
