package stars.webapi.impl

import akka.stream.scaladsl.Source
import akka.stream.{Materializer, OverflowStrategy, QueueOfferResult}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import stars.webapi.{SimulationService, SimulatorService}
import stars.webapi.protocol.{SimulationOrderID, SimulationOrderResponse}

import scala.concurrent.Future

class SimulationServiceImpl(simulator: SimulatorService)(implicit materializer: Materializer) extends SimulationService {

  import materializer.executionContext

  private val (queue, source) = Source.queue[SimulationOrderID](10, OverflowStrategy.fail).preMaterialize

  //noinspection TypeAnnotation
  override def simulate = ServiceCall { order =>
    val orderID = SimulationOrderID(order)
    for {
      queueResult <- queue.offer(orderID)
      orderResponse <- waitOrderResponse(orderID, queueResult)
    } yield {
      orderResponse
    }
  }

  override def simulationOrderTopic: Topic[SimulationOrderID] = {
    TopicProducer.singleStreamWithOffset { offset =>
      source.map(order => (order, offset))
    }
  }

  private def waitOrderResponse(orderID: SimulationOrderID, result: QueueOfferResult): Future[SimulationOrderResponse] = {
    ???
  }
}
