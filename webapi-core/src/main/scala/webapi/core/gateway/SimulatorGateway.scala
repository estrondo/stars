package webapi.core.gateway

import webapi.core.{SimulationDescription, SimulationReceipt}

import scala.concurrent.Future

trait SimulatorGateway {

  def send(id: String, description: SimulationDescription): Future[SimulationReceipt]

}
