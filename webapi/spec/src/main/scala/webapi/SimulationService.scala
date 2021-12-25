package webapi

import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import webapi.protocol.{CreateSimulation, CreateSimulationResponse}

trait SimulationService extends Service {

  override def descriptor: Descriptor = {
    import Service._
    named("stars-webapi")
      .withCalls(
        restCall(Method.POST, "/simulate", simulate)
      )
      .withAutoAcl(true)
  }

  def simulate: ServiceCall[CreateSimulation, CreateSimulationResponse]
}
