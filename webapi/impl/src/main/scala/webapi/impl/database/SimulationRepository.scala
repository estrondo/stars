package webapi.impl.database

import webapi.impl.model.Simulation

import java.util.UUID
import scala.concurrent.Future

trait SimulationRepository {

  def add(simulation: Simulation): Future[Simulation]

  def get(id: UUID): Future[Option[Simulation]]

}


