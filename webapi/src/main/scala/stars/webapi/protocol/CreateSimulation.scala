package stars.webapi.protocol

case class CreateSimulation(
  name: String,
  email: String,
  segments: Seq[Segment],
  minStars: Option[Int],
  maxStars: Option[Int],
  minStarWeight: Option[SolarMass],
  maxStarWeight: Option[SolarMass],
  starWeightDistribution: Option[Int],
  blackHoles: Seq[BlackHoleOrder]
)