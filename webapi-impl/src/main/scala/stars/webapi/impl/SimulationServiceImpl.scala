package stars.webapi.impl

import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef}
import akka.util.Timeout
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.deser.ExceptionSerializer
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import com.typesafe.scalalogging.StrictLogging
import stars.webapi.impl.persistence.{SimulationCommand, SimulationEvent, SimulationPersistence}
import stars.webapi.protocol.{CreateSimulationResponse, SimulationOrder}
import stars.webapi.{SimulationService, SimulatorService}

import java.time.Instant
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

class SimulationServiceImpl(
  simulator: SimulatorService,
  clusterSharding: ClusterSharding,
  persistentRegistry: PersistentEntityRegistry)(implicit executor: ExecutionContextExecutor)
  extends SimulationService with StrictLogging {

  private implicit val timeout: Timeout = Timeout(5.seconds)

  //noinspection TypeAnnotation
  override def simulate = ServiceCall { command =>
    logger.info("Received a new simulation order.")
    val order = SimulationOrder(command)
    for {
      response <- entityFor(order.id.toString).ask(SimulationCommand.Create(order, _))
    } yield response match {
      case Right(SimulationOrder(id, order)) => CreateSimulationResponse(id, Instant.now(), order)
      case Left((_, cause)) => throw new IllegalStateException(cause)
    }
  }

  override def simulationOrderTopic: Topic[SimulationOrder] = {
    TopicProducer.taggedStreamWithOffset(SimulationEvent.Tag) { (tag, offset) =>
      persistentRegistry.eventStream(tag, offset)
        .collect {
          case EventStreamElement(_, SimulationEvent.Created(order), offset) => (order, offset)
        }
    }
  }

  private def entityFor(id: String): EntityRef[SimulationCommand] = {
    clusterSharding.entityRefFor(SimulationPersistence.typeKey, id)
  }
}
