package stars.webapi.impl

import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef}
import akka.util.Timeout
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.typesafe.scalalogging.StrictLogging
import stars.webapi.impl.persistence.{SimulationCommand, SimulationStore}
import stars.webapi.protocol.{SimulationOrderID, SimulationOrderResponse}
import stars.webapi.{SimulationService, SimulatorService}

import java.time.Instant
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

class SimulationServiceImpl(simulator: SimulatorService, clusterSharding: ClusterSharding)(implicit executor: ExecutionContextExecutor)
  extends SimulationService with StrictLogging {

  private implicit val timeout: Timeout = Timeout(5.seconds)

  //noinspection TypeAnnotation
  override def simulate = ServiceCall { order =>
    logger.info("Received a new simulation order.")
    val issuedAt = Instant.now()
    val orderID = SimulationOrderID(order)
    for {
      response <- entityFor(orderID.id.toString)
        .ask(SimulationCommand.Create(orderID, _))
    } yield response match {
      case Right(SimulationOrderID(id, order)) => SimulationOrderResponse(id, issuedAt, Instant.now(), order)
      case Left((_, cause)) => throw new IllegalStateException(cause)
    }
  }

  //  override def simulationOrderTopic: Topic[SimulationOrderID] = {
  //    TopicProducer.singleStreamWithOffset { offset =>
  //      ???
  //    }
  //  }

  private def entityFor(id: String): EntityRef[SimulationCommand] = {
    clusterSharding.entityRefFor(SimulationStore.typeKey, id)
  }
}
