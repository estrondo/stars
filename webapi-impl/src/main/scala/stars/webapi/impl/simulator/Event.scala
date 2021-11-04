package stars.webapi.impl.simulator

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger}
import stars.simulation.protocol.NewSimulation

object Event {

  case class New(command: NewSimulation) extends Event

  val Tag = AggregateEventTag.sharded[Event](numShards = 5)
}

sealed trait Event extends AggregateEvent[Event] {

  override def aggregateTag: AggregateEventTagger[Event] = Event.Tag
}
