package stars.webapi.impl.mapper

import io.scalaland.chimney.dsl._
import stars.simulation.protocol.Description
import stars.webapi.protocol.CreateSimulation

object SimulationToWebAPI {

  def createSimulation(input: Option[Description]): CreateSimulation = input match {
    case Some(value) => createSimulation(value)
    case _ => throw new IllegalArgumentException("There is no simulation description!")
  }

  def createSimulation(input: Description): CreateSimulation = {
    input
      .into[CreateSimulation]
      .withFieldComputed(_.minStarMass, x => Some(x.minStarMass))
      .withFieldComputed(_.maxStarMass, x => Some(x.maxStarMass))
      .withFieldComputed(_.massDistribution, x => Some(x.massDistribution))
      .withFieldComputed(_.segments, _.segments.map(x => x.points))
      .transform
  }
}
