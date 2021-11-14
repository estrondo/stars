package stars.webapi

import com.lightbend.lagom.scaladsl.api._
import com.lightbend.lagom.scaladsl.api.transport.Method
import stars.webapi.protocol._

trait SimulationService extends Service {

  override def descriptor: Descriptor = {
    import Service._
    named("stars-simulation-webapi")
      .withCalls(
        restCall(Method.POST, "/simulate", simulate)
      )
      .withAutoAcl(true)
  }

  def simulate: ServiceCall[CreateSimulation, CreateSimulationResponse]
}
