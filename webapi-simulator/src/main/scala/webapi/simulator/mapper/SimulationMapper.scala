package webapi.simulator.mapper

import io.scalaland.chimney.Transformer
import simulator.protocol.{BlackHole, Branch, Simulation, Viewport, ViewportVector}
import webapi.core.{
  BlackHoleDescription,
  BranchDescription,
  SimulationDescription,
  ViewportDescription,
  ViewportVectorDescription
}
import io.scalaland.chimney.dsl._

import scala.annotation.nowarn

object SimulationMapper {

  // noinspection ScalaUnusedSymbol
  private val fromBlackHoleDescription: Transformer[BlackHoleDescription, BlackHole] = { description =>
    description
      .into[BlackHole]
      .disableDefaultValues
      .withFieldConst(_.unknownFields, scalapb.UnknownFieldSet.empty)
      .transform
  }

  // noinspection ScalaUnusedSymbol
  private val fromBranchDescription: Transformer[BranchDescription, Branch] = { description =>
    description
      .into[Branch]
      .disableDefaultValues
      .withFieldConst(_.unknownFields, scalapb.UnknownFieldSet.empty)
      .transform
  }

  private val fromViewportVectorDescription: Transformer[ViewportVectorDescription, ViewportVector] = { description =>
    //noinspection ScalaUnusedSymbol
    description
      .into[ViewportVector]
      .disableDefaultValues
      .withFieldConst(_.unknownFields, scalapb.UnknownFieldSet.empty)
      .transform
  }

  // noinspection ScalaUnusedSymbol
  private val fromViewportDescription: Transformer[ViewportDescription, Viewport] = { description =>
    @nowarn implicit val i0 = fromViewportVectorDescription
    description
      .into[Viewport]
      .disableDefaultValues
      .withFieldConst(_.unknownFields, scalapb.UnknownFieldSet.empty)
      .transform
  }

  // noinspection TypeAnnotation
  def fromSimulationDescription(id: String, description: SimulationDescription): Simulation = {
    @nowarn implicit val t1 = fromBlackHoleDescription
    @nowarn implicit val t2 = fromBranchDescription
    @nowarn implicit val t3 = fromViewportDescription
    description
      .into[Simulation]
      .disableDefaultValues
      .withFieldConst(_.id, id)
      .withFieldConst(_.unknownFields, scalapb.UnknownFieldSet.empty)
      .transform
  }
}
