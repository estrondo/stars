package webapi.core.fixture

import webapi.core.service.impl.SimulatorServiceImpl

object SimulatorServiceFixture {

  def newConfiguration(): SimulatorServiceImpl.Configuration =
    SimulatorServiceImpl.Configuration(
      maxStars = 10000,
      defaultStars = 1000,
      minStarMass = 0.1,
      maxStarMass = 100,
      minBlackHoleMass = 4,
      maxBlackHoleMass = 1000
    )
}
