package webapi

import play.api.libs.json.{Json, OFormat}

package object protocol {

  type Segment = Seq[Double]

  implicit val blackHoleFormat: OFormat[BlackHole] = Json.format[BlackHole]

  implicit val createSimulationFormat: OFormat[CreateSimulation] = Json.format[CreateSimulation]

  implicit val createSimulationResponseFormat: OFormat[CreateSimulationResponse] = Json.format[CreateSimulationResponse]

}
