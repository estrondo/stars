package webapi.impl.simulator

import simulation.protocol.Simulation

object State {

  object Empty extends State

  case class Sent(simulation: Simulation) extends State
}

sealed trait State

