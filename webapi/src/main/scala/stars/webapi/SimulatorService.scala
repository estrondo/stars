package stars.webapi

import com.lightbend.lagom.scaladsl.api.{Descriptor, Service}

object SimulatorService {

  val SimulationOrderResponseTopic = "simulation-order-response"
}

trait SimulatorService extends Service {

  override def descriptor: Descriptor = {
    import Service._
    named("simulator")
  }
}
