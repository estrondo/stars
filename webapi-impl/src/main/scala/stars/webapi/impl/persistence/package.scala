package stars.webapi.impl

import play.api.libs.json.{Json, OFormat}

package object persistence {

  implicit val waitingFormat: OFormat[SimulationState.Waiting] = Json.format[SimulationState.Waiting]
}
