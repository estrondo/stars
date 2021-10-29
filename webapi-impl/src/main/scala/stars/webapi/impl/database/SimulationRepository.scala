package stars.webapi.impl.database

import com.typesafe.scalalogging.StrictLogging
import stars.webapi.SimulationException
import stars.webapi.impl.RichFuture
import stars.webapi.impl.database.API._
import stars.webapi.impl.model.Simulation

import scala.concurrent.{ExecutionContextExecutor, Future}

trait SimulationRepository {

  def add(simulation: Simulation): Future[Simulation]

}

class SimulationRepositoryImpl(database: Database)(implicit executor: ExecutionContextExecutor)
  extends SimulationRepository with StrictLogging {

  def add(simulation: Simulation): Future[Simulation] = {
    for (_ <- database.run(SimulationTable += simulation)) yield {
      logger.debug("Added simulation {}.", simulation)
      simulation
    }
  } throwWith {
    SimulationException.Unexpected(s"Impossible to add simulation ${simulation.id}!", _)
  }
}
