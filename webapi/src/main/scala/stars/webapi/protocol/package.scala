package stars.webapi

import play.api.libs.json.{Json, OFormat}

package object protocol {

  type Segment = Seq[Double]

  type SolarMass = Double

  implicit val blackHoleOrderFormat: OFormat[BlackHoleOrder] = Json.format[BlackHoleOrder]

  implicit val simulationOrderFormat: OFormat[SimulationOrder] = Json.format[SimulationOrder]

  implicit val simulationOrderResponseFormat: OFormat[SimulationOrderResponse] = Json.format[SimulationOrderResponse]

  implicit val simulationOrderIDFormat: OFormat[SimulationOrderID] = Json.format[SimulationOrderID]

}
