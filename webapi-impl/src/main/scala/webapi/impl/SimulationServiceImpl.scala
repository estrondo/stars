package webapi.impl

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.util.Timeout
import com.lightbend.lagom.scaladsl.api.transport.{MessageProtocol, ResponseHeader}
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import com.typesafe.scalalogging.StrictLogging
import webapi.impl.mapper.SimulationMapper
import webapi.impl.simulator.Command
import webapi.{SimulationException, SimulationService, protocol}

import scala.concurrent.ExecutionContextExecutor

class SimulationServiceImpl(
  region: ActorRef[ShardingEnvelope[Command]]
)(implicit executor: ExecutionContextExecutor, timeout: Timeout, system: ActorSystem[_]) extends SimulationService with StrictLogging {

  override def simulate = ServerServiceCall { (_, createSimulation) =>

    val id = generateRandomID()
    logger.debug("Receive a new simulation, it will have id {}.", id)

    val simulation = SimulationMapper.fromCreateSimulation(id, createSimulation)
    val future = region.ask[Command.CreateResponse](x => ShardingEnvelope(id, Command.Create(simulation, x)))

    for (Command.CreateResponse(simulation, error) <- future) yield error match {
      case None =>
        logger.debug("Simulation {} was accepted.", id)
        acceptedHeader -> protocol.CreateSimulationResponse(id, SimulationMapper.toCreateSimulation(simulation))

      case Some(cause) =>
        logger.warn("An error on simulation {}!", id, cause)
        throw SimulationException.Unexpected("Impossible to accept!", cause)
    }
  }

  private def acceptedHeader = ResponseHeader(202, MessageProtocol.empty, Nil)
}
