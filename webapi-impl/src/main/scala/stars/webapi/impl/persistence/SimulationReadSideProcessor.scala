package stars.webapi.impl.persistence

import akka.stream.scaladsl.Flow
import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor.ReadSideHandler
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}
import stars.webapi.impl.database.SimulationRepository
import stars.webapi.impl.model.Simulation
import stars.webapi.impl.persistence.SimulationEvent.Created
import stars.webapi.impl.{FutureDone, RichFuture}
import stars.webapi.protocol.SimulationOrder

import scala.concurrent.{ExecutionContextExecutor, Future}

class SimulationReadSideProcessor(repository: SimulationRepository)(implicit executor: ExecutionContextExecutor)
  extends ReadSideProcessor[SimulationEvent] {

  override def aggregateTags: Set[AggregateEventTag[SimulationEvent]] = SimulationEvent.Tag.allTags

  //noinspection ConvertExpressionToSAM
  override def buildHandler(): ReadSideHandler[SimulationEvent] = new ReadSideHandler[SimulationEvent] {

    override def handle(): Flow[EventStreamElement[SimulationEvent], Done, NotUsed] = {
      Flow[EventStreamElement[SimulationEvent]]
        .mapAsync(1) { element =>
          element.event match {
            case Created(order) => created(order)
            case _ => FutureDone
          }
        }
    }
  }

  private def created(order: SimulationOrder): Future[Done] = {
    repository.add(Simulation.from(order)).toDone
  }
}
