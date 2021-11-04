package stars.webapi.impl.fixture

import stars.webapi.protocol.{BlackHole, CreateSimulation, SimulationOrder}


object CreateSimulationFixture {

  def newCreateSimulation(): CreateSimulation = {
    val id = newRandomId()
    CreateSimulation(
      name = "Albert Einstein",
      email = s"albert-$id@einstein.org",
      segments = Nil,
      stars = Some(10),
      minStarWeight = Some(0.1),
      maxStarWeight = Some(10),
      weightDistribution = Some(0),
      blackHoles = Seq(BlackHole(100, 0, 0, 0))
    )
  }
}



trait SingleCreateSimulationFixture {

  val createSimulation: CreateSimulation = CreateSimulationFixture.newCreateSimulation()
}

trait SingleSimulationOrder extends SingleCreateSimulationFixture {

  val simulationOrder: SimulationOrder = SimulationOrder(createSimulation)
}
