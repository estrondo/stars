package stars.webapi.impl.mapper

import io.scalaland.chimney.dsl._
import stars.simulation.protocol.{Description, NewSimulation}
import stars.webapi.protocol.CreateSimulation

object WebAPIToSimulation {

  def newSimulation(id: String, input: CreateSimulation): NewSimulation = {
    val description = input
      .into[Description]
      .withFieldComputed(_.stars, _.stars.getOrElse(0))
      .withFieldComputed(_.minStarMass, _.minStarMass.getOrElse(0D))
      .withFieldComputed(_.maxStarMass, _.maxStarMass.getOrElse(0D))
      .withFieldComputed(_.massDistribution, _.massDistribution.getOrElse(0D))
      .transform

    NewSimulation(id, Some(description))
  }
}
