package webapi.core.service

import webapi.core.{SimulationDescription, SimulationReceipt}

import scala.concurrent.Future

trait SimulatorService {

  def register(description: SimulationDescription): Future[SimulationReceipt]
}
