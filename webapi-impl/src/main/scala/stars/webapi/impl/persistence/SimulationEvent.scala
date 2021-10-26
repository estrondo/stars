package stars.webapi.impl.persistence

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger}
import stars.webapi.protocol.SimulationOrderID

object SimulationEvent {

  case class Created(order: SimulationOrderID) extends SimulationEvent

  //noinspection TypeAnnotation
  val Tag = AggregateEventTag.sharded[SimulationEvent](numShards = 10)
}

sealed trait SimulationEvent extends AggregateEvent[SimulationEvent] {

  override def aggregateTag: AggregateEventTagger[SimulationEvent] = SimulationEvent.Tag
}
