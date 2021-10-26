package stars.webapi.impl

import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomServer}
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents
import stars.webapi.{SimulationService, SimulatorService}

abstract class Application(context: LagomApplicationContext)
  extends LagomApplication(context)
    with LagomKafkaComponents
    with AhcWSComponents {

  lazy val simulatorService: SimulatorService = serviceClient.implement[SimulatorService]

  override def lagomServer: LagomServer = serverFor[SimulationService](wire[SimulationServiceImpl])
}
