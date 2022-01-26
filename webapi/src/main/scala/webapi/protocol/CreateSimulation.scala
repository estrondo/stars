package webapi.protocol

case class CreateSimulation(
    name: String,
    email: String,
    stars: Option[Int],
    minStarMass: Option[Double],
    maxStarMass: Option[Double],
    massDistribution: Option[Double],
    blackHoles: Seq[CreateBlackHole],
    branches: Seq[CreateBranch],
    viewports: Seq[CreateViewport]
)
