package stars.webapi.impl.infra

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import stars.webapi.protocol.{CreateSimulation, CreateSimulationResponse}

class SimulationJsonRegistry extends JsonSerializerRegistry {

  override def serializers: Seq[JsonSerializer[_]] = Vector(
    JsonSerializer[CreateSimulation],
    JsonSerializer[CreateSimulationResponse]
  )
}
