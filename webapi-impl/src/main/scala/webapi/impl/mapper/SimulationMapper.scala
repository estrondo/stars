package webapi.impl.mapper

import io.scalaland.chimney.dsl._
import simulation.protocol.Simulation
import webapi.protocol.CreateSimulation

object SimulationMapper {

  def fromCreateSimulation(id: String, input: CreateSimulation): Simulation = {
    input
      .into[Simulation]
      .withFieldConst(_.id, id)
      .withFieldComputed(_.stars, _.stars.getOrElse(0))
      .withFieldComputed(_.minStarMass, _.minStarMass.getOrElse(0D))
      .withFieldComputed(_.maxStarMass, _.maxStarMass.getOrElse(0D))
      .withFieldComputed(_.massDistribution, _.massDistribution.getOrElse(0D))
      .transform
  }

  def toCreateSimulation(input: Simulation): CreateSimulation = {
    input
      .into[CreateSimulation]
      .withFieldComputed(_.segments, s => for (c <- s.segments) yield c.coordinates)
      .transform
  }
}
