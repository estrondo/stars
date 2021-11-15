package stars.simulator.entity

import stars.simulation.protocol.Description

trait Event

object Event {

  case class Accepted(id: String, description: Description) extends Event
}
