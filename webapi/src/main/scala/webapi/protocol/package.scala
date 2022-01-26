package webapi

import spray.json._
import spray.json.DefaultJsonProtocol._

package object protocol {

  implicit val CreateBranchFormat: RootJsonFormat[CreateBranch] =
    jsonFormat1(CreateBranch)

  implicit val CreateBlackHoleFormat: RootJsonFormat[CreateBlackHole] =
    jsonFormat4(CreateBlackHole)

  implicit val CreateViewportVectorFormat: RootJsonFormat[CreateViewportVector] =
    jsonFormat3(CreateViewportVector)

  implicit val CreateViewportFormat: RootJsonFormat[CreateViewport] =
    jsonFormat9(CreateViewport)

  implicit val CreateSimulationFormat: RootJsonFormat[CreateSimulation] =
    jsonFormat9(CreateSimulation)

  implicit val CreateSimulationResponseFormat: RootJsonFormat[CreateSimulationResponse] =
    jsonFormat1(CreateSimulationResponse)
}
