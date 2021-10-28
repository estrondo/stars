package stars.webapi.impl.infra

import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomServer}
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents
import stars.webapi.impl.SimulationServiceImpl
import stars.webapi.impl.persistence.SimulationStoreComponents
import stars.webapi.{SimulationService, SimulatorService}

import scala.concurrent.ExecutionContextExecutor

abstract class Application(context: LagomApplicationContext)
  extends LagomApplication(context)
    with LagomKafkaComponents
    with SimulationStoreComponents
    with AhcWSComponents {

  lazy val simulatorService: SimulatorService = serviceClient.implement[SimulatorService]

  protected implicit def executor: ExecutionContextExecutor = actorSystem.dispatcher

  override def lagomServer: LagomServer = serverFor[SimulationService](wire[SimulationServiceImpl])
}
