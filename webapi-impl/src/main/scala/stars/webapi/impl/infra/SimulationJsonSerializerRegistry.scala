package stars.webapi.impl.infra

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import stars.webapi.impl.persistence.SimulationEvent

object SimulationJsonSerializerRegistry extends JsonSerializerRegistry {

  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[SimulationEvent.Created]
  )
}
