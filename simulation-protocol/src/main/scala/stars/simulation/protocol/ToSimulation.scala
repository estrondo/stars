package stars.simulation.protocol

import io.scalaland.chimney.dsl._

object ToSimulation {

  def apply(input: NewSimulation): Simulation = {
    input.transformInto[Simulation]
  }
}
