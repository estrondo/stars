package stars.webapi.impl.infra

import akka.cluster.sharding.typed.scaladsl.EntityRef
import akka.util.Timeout
import com.lightbend.lagom.scaladsl.playjson.RequiresJsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server.{LagomServer, LagomServerComponents}
import com.softwaremill.macwire.wire
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import stars.webapi.SimulationService
import stars.webapi.impl.SimulationServiceImpl
import stars.webapi.impl.simulator.Command

import java.util.UUID
import scala.concurrent.duration.FiniteDuration

trait WebAPIComponent extends RequiresJsonSerializerRegistry {
  this: LagomServerComponents with SimulatorComponentRequired =>

  implicit val timeout = Timeout(config.as[FiniteDuration]("stars.webapi.timeout"))

  protected implicit val executor = actorSystem.dispatcher

  def config: Config

  override def jsonSerializerRegistry =
    new SimulationJsonRegistry

  override def lagomServer: LagomServer = serverFor[SimulationService](wire[SimulationServiceImpl])
}

trait SimulatorComponentRequired {

  def getSimulatorEntityRef: UUID => EntityRef[Command]
}
