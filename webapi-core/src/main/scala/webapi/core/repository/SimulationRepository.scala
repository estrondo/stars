package webapi.core.repository

import webapi.core.Simulation

import scala.concurrent.Future

trait SimulationRepository {

  def allocate(id: String): Future[Simulation]
}
