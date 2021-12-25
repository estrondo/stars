package webapi.impl.simulator

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger}
import simulation.protocol.Simulation

object Event {

  case class Created(simulation: Simulation) extends Event

  val Tag = AggregateEventTag.sharded[Event](numShards = 5)
}

sealed trait Event extends AggregateEvent[Event] {

  override def aggregateTag: AggregateEventTagger[Event] = Event.Tag
}
