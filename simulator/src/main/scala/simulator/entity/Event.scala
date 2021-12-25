package simulator.entity

import simulation.protocol.Simulation

trait Event

object Event {

  case class Accepted(simulation: Simulation) extends Event
}
