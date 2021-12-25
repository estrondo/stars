package webapi.impl.component

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import webapi.protocol.{CreateSimulation, CreateSimulationResponse}

class SimulationJsonRegistry extends JsonSerializerRegistry {

  override def serializers: Seq[JsonSerializer[_]] = Vector(
    JsonSerializer[CreateSimulation],
    JsonSerializer[CreateSimulationResponse]
  )
}
