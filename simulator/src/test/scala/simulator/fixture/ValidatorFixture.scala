package simulator.fixture

import simulator.entity.Validator

object ValidatorFixture {

  def newValidator(): Validator = Validator(
    minStarMass = 0.1,
    maxStarMass = 10,
    maxStars = 1000,
    maxBlackHoleMass = 50
  )

}
