package stars.webapi.protocol

import java.util.UUID

case class CreateSimulationResponse(
  id: UUID,
  simulation: CreateSimulation
)
