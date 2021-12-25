package webapi.impl.simulator

import simulator.protocol.Simulation


object State {

  object Empty extends State

  case class Processing(simulation: Simulation) extends State

  case class Sent(simulation: Simulation) extends State
}

sealed trait State

