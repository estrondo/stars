package stars.webapi.impl.fixture

import stars.webapi.protocol.{BlackHole, CreateSimulation, SimulationOrder}


object SingleCreateSimulation {

  def newCreateSimulation: CreateSimulation = CreateSimulation(
    name = "Albert Einstein",
    email = "albert@einstein.org",
    segments = Nil,
    stars = Some(10),
    minStarWeight = Some(0.1),
    maxStarWeight = Some(10),
    starWeightDistribution = None,
    blackHoles = Seq(BlackHole(100, 0, 0, 0))
  )
}

trait SingleCreateSimulation {

  val createSimulation: CreateSimulation = SingleCreateSimulation.newCreateSimulation
}

trait SingleSimulationOrder extends SingleCreateSimulation {

  val simulationOrder: SimulationOrder = SimulationOrder(createSimulation)
}
