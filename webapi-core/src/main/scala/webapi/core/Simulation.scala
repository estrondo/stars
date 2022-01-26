package webapi.core

import webapi.core.Simulation.State

object Simulation {

  case object Empty extends State

  sealed trait State
}

case class Simulation(id: String, state: State)
