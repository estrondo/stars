package stars.webapi.impl.mapper

import io.scalaland.chimney.dsl._
import stars.simulation.protocol.{Description, NewSimulation}
import stars.webapi.protocol.CreateSimulation

object WebAPIToSimulation {

  def newSimulation(id: String, input: CreateSimulation): NewSimulation = {
    val description = input
      .into[Description]
      .withFieldComputed(_.stars, _.stars.getOrElse(0))
      .withFieldComputed(_.minStarWeight, _.minStarWeight.getOrElse(0D))
      .withFieldComputed(_.maxStarWeight, _.maxStarWeight.getOrElse(0D))
      .withFieldComputed(_.weightDistribution, _.weightDistribution.getOrElse(0D))
      .transform

    NewSimulation(id, Some(description))
  }
}
