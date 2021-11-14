package stars.simulation.protocol

import io.scalaland.chimney.dsl._

object ToSimulation {

  def fromNewSimulation(input: NewSimulation): Simulation = {
    input.transformInto[Simulation]
  }
}
