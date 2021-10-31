package stars.webapi.impl.database

import com.typesafe.scalalogging.StrictLogging
import stars.webapi.SimulationException
import stars.webapi.impl.RichFuture
import stars.webapi.impl.database.API._
import stars.webapi.impl.model.Simulation

import java.util.UUID
import scala.concurrent.{ExecutionContextExecutor, Future}

trait SimulationRepository {

  def add(simulation: Simulation): Future[Simulation]

  def get(id: UUID): Future[Option[Simulation]]

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

  override def get(id: UUID): Future[Option[Simulation]] = {
    val query = for (row <- SimulationTable if row.id === id) yield row
    database.run(query.result.headOption)
      .throwWith(SimulationException.Unexpected(s"Impossible get simulation $id!", _))
  }
}
