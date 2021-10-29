package stars.webapi

import com.lightbend.lagom.scaladsl.api._
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.transport.Method
import stars.webapi.SimulationService.SimulationOrderTopicName
import stars.webapi.protocol._

object SimulationService {

  val SimulationOrderTopicName = "simulation-order-id"
}

trait SimulationService extends Service {

  override def descriptor: Descriptor = {
    import Service._
    named("/simulation")
      .withCalls(
        restCall(Method.POST, "/simulate", simulate)
      )
      .withAutoAcl(true)
      .withTopics(
        topic(SimulationOrderTopicName, simulationOrderTopic)
      )
  }

  def simulate: ServiceCall[CreateSimulation, CreateSimulationResponse]

  def simulationOrderTopic: Topic[SimulationOrder]
}
