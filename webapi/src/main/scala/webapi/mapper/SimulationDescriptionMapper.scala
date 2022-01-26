package webapi.mapper

import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import simulator.protocol.{Viewport, ViewportVector}
import webapi.core.SimulationDescription
import webapi.protocol.{CreateSimulation, CreateViewport, CreateViewportVector}

import scala.annotation.nowarn

object SimulationDescriptionMapper {

  @nowarn
  private implicit val optionDoubleToDouble: Transformer[Option[Double], Double] = {
    case Some(value) => value
    case None        => 0
  }
  @nowarn
  private implicit val optionIntToInt: Transformer[Option[Int], Int] = {
    case Some(value) => value
    case None        => 0
  }

  private val fromCreateViewportVector: Transformer[CreateViewportVector, ViewportVector] = { create =>
    create
      .into[ViewportVector]
      .disableDefaultValues
      .withFieldConst(_.unknownFields, scalapb.UnknownFieldSet.empty)
      .transform
  }

  private val fromCreateViewport: Transformer[CreateViewport, Viewport] = { create =>
    //noinspection TypeAnnotation
    @nowarn implicit val i0 = fromCreateViewportVector
    create
      .into[Viewport]
      .disableDefaultValues
      .withFieldConst(_.unknownFields, scalapb.UnknownFieldSet.empty)
      .transform
  }

  def fromCreateSimulation(command: CreateSimulation): SimulationDescription = {
    //noinspection TypeAnnotation
    @nowarn implicit val i0 = fromCreateViewport
    command
      .into[SimulationDescription]
      .disableDefaultValues
      .transform
  }

  def toCreateSimulation(
      description: SimulationDescription
  ): CreateSimulation = {
    description.transformInto[CreateSimulation]
  }
}
