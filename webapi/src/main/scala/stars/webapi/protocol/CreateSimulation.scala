package stars.webapi.protocol

case class CreateSimulation(
  name: String,
  email: String,
  segments: Seq[Segment],
  stars: Option[Int],
  minStarWeight: Option[SolarMass],
  maxStarWeight: Option[SolarMass],
  weightDistribution: Option[Double],
  blackHoles: Seq[BlackHole]
)
