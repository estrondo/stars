package stars.webapi.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import stars.webapi.impl.persistence.SimulationState

object SimulationJsonSerializerRegistry extends JsonSerializerRegistry{

  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[SimulationState.Waiting]
  )
}
