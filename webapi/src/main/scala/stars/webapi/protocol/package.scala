package stars.webapi

import play.api.libs.json.{Json, OFormat}

package object protocol {

  type Segment = Seq[Double]

  type SolarMass = Double

  type Pc = Double

  implicit val blackHoleOrderFormat: OFormat[BlackHole] = Json.format[BlackHole]

  implicit val simulationOrderFormat: OFormat[CreateSimulation] = Json.format[CreateSimulation]

  implicit val simulationOrderResponseFormat: OFormat[CreateSimulationResponse] = Json.format[CreateSimulationResponse]

  implicit val simulationOrderIDFormat: OFormat[SimulationOrder] = Json.format[SimulationOrder]

}
