package simulator.impl.entity

import simulator.protocol.Simulation

trait Event

object Event {

  case class Accepted(simulation: Simulation) extends Event
}
