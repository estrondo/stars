package stars.testkit.fixture.webapi

import stars.textkit.newRandomId
import stars.webapi.protocol.{BlackHole, CreateSimulation}

object CreateSimulationFixture {

  def newCreateSimulation(): CreateSimulation = {
    val id = newRandomId()
    CreateSimulation(
      name = "Albert Einstein",
      email = s"albert-$id@einstein.org",
      segments = Nil,
      stars = Some(10),
      minStarMass = Some(0.1),
      maxStarMass = Some(10),
      massDistribution = Some(0),
      blackHoles = Seq(BlackHole(100, 0, 0, 0))
    )
  }
}
