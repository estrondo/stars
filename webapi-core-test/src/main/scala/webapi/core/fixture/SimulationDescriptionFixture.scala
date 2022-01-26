package webapi.core.fixture

import webapi.core.{BlackHoleDescription, BranchDescription, SimulationDescription}

object SimulationDescriptionFixture {

  def newDescription(): SimulationDescription = SimulationDescription(
    name = "my first simulation",
    email = "albert@einstein.com",
    stars = 100,
    minStarMass = 0.5,
    maxStarMass = 10,
    0,
    blackHoles = Seq(
      BlackHoleDescription(
        solarMass = 50,
        x = 0,
        y = 0,
        z = 0
      )
    ),
    branches = Seq(
      BranchDescription(Seq(800, 800, -400, 0, 0, 0,   -400, -400, 800))
    ),
    viewports = Seq.empty
  )
}
