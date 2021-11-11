package stars.webapi.impl.fixture

import stars.simulation.protocol.{BlackHole, Description, NewSimulation}

object SimulationFixture {

  def newDescription(id: String = newRandomId()): Description = {
    Description(
      name = s"Galileo $id",
      email = s"galileo-$id@science.org",
      stars = 10,
      minStarWeight = 0.1,
      maxStarWeight = 10,
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

