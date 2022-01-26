package webapi.core

case class SimulationDescription(
    name: String,
    email: String,
    stars: Int,
    minStarMass: Double,
    maxStarMass: Double,
    massDistribution: Double,
    blackHoles: Seq[BlackHoleDescription],
    branches: Seq[BranchDescription],
    viewports: Seq[ViewportDescription]
)
