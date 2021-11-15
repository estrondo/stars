package stars.testkit.fixture.simulation

import stars.simulation.protocol.{BlackHole, Description, NewSimulation}
import stars.textkit.newRandomId

object SimulationFixture {

  def newDescription(id: String = newRandomId()): Description = {
    Description(
      name = s"Galileo $id",
      email = s"galileo-$id@science.org",
      stars = 10,
      minStarMass = 0.1,
      maxStarMass = 10,
      blackHoles = Seq(BlackHole(50, 0, 0, 0))
    )
  }

  def newNewSimulation(id: String = newRandomId()): NewSimulation = {
    NewSimulation(
      id = id,
      description = Some(newDescription(id))
    )
  }
}
