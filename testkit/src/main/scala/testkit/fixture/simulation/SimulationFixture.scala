package testkit.fixture.simulation

import simulation.protocol.Simulation
import textkit.newRandomId

object SimulationFixture {

  def newNewSimulation(id: String = newRandomId()): Simulation = {
    Simulation(
      id = id,
      name = s"Isaac Newton $id",
      email = s"newton-$id@graviton.org",
      stars = 1000,
      minStarMass = 0.1,
      maxStarMass = 10,
      blackHoles = Nil,
      segments = Nil,
      viewports = Nil
    )
  }
}
