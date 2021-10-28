package stars.webapi.impl

import play.api.libs.json.{Json, OFormat}

package object persistence {

  implicit val simulationEvent: OFormat[SimulationEvent.Created] = Json.format[SimulationEvent.Created]
}
