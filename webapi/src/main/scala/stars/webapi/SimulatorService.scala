package stars.webapi

import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service}
import stars.webapi.SimulatorService.SimulationOrderResponseTopic
import stars.webapi.protocol.SimulationOrderResponse

object SimulatorService {

  val SimulationOrderResponseTopic = "simulation-order-response"
}

trait SimulatorService extends Service {

  override def descriptor: Descriptor = {
    import Service._
    named("simulator")
      .withTopics(topic(SimulationOrderResponseTopic, orderTopic))
  }

  def orderTopic: Topic[SimulationOrderResponse]
}
