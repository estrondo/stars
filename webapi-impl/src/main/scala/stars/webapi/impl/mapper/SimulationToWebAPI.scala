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
      .withFieldComputed(_.minStarWeight, x => Some(x.minStarWeight))
      .withFieldComputed(_.maxStarWeight, x => Some(x.maxStarWeight))
      .withFieldComputed(_.weightDistribution, x => Some(x.weightDistribution))
      .withFieldComputed(_.segments, _.segments.map(x => x.points))
      .transform
  }
}
