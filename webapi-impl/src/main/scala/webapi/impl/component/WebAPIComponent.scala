package webapi.impl.component

import akka.actor.typed.ActorSystem
import akka.util.Timeout
import com.lightbend.lagom.scaladsl.playjson.RequiresJsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server.{LagomServer, LagomServerComponents}
import com.softwaremill.macwire.wire
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import webapi.impl.SimulationServiceImpl
import webapi.SimulationService

import scala.concurrent.duration.FiniteDuration

trait WebAPIComponent extends RequiresJsonSerializerRegistry {
  this: LagomServerComponents
    with RequiresCommandRegionComponent =>

  implicit val timeout = Timeout(config.as[FiniteDuration]("stars.webapi.timeout"))

  protected implicit val executor = actorSystem.dispatcher

  def config: Config

  override def jsonSerializerRegistry =
    new SimulationJsonRegistry

  override def lagomServer: LagomServer = serverFor[SimulationService](wire[SimulationServiceImpl])

  protected implicit def typedActorSystem: ActorSystem[_] = ActorSystem.wrap(actorSystem)
}
