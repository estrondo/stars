package webapi.protocol

import simulation.protocol.SolarMass

case class CreateSimulation(
  name: String,
  email: String,
  segments: Seq[Segment],
  stars: Option[Int],
  minStarMass: Option[SolarMass],
  maxStarMass: Option[SolarMass],
  massDistribution: Option[Double],
  blackHoles: Seq[BlackHole]
)
