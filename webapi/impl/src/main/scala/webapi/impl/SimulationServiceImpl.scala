package webapi.impl

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.util.Timeout
import com.lightbend.lagom.scaladsl.api.transport.{MessageProtocol, ResponseHeader}
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import com.typesafe.scalalogging.StrictLogging
import webapi.impl.mapper.SimulationMapper
import webapi.impl.protocol.Attempt
import webapi.impl.simulator.Command
import webapi.{SimulationException, SimulationService, protocol}

import scala.concurrent.ExecutionContextExecutor

class SimulationServiceImpl(
  commandRegionRef: ActorRef[ShardingEnvelope[Command]]
)(implicit executor: ExecutionContextExecutor, timeout: Timeout, system: ActorSystem[_]) extends SimulationService with StrictLogging {

  //noinspection TypeAnnotation
  override def simulate = ServerServiceCall { (_, createSimulation) =>
    val simulation = SimulationMapper.fromCreateSimulation(generateRandomID(), createSimulation)
    val response = commandRegionRef.ask[Command.CreateResponse] {
      x => ShardingEnvelope(simulation.id, Command.Create(simulation, x))
    }

    logger.debug("A new simulation was received, it will have id {}.", simulation.id)

    for (Command.CreateResponse(content) <- response) yield content match {
      case Attempt.Success(accepted) =>
        logger.debug("Simulation {} was accepted.", accepted.id)
        acceptedHeader -> protocol.CreateSimulationResponse(accepted.id, SimulationMapper.toCreateSimulation(accepted))

      case Attempt.Failure(rejected, cause) =>
        logger.warn("An error occurred on simulation {}!", rejected.id, cause)
        throw SimulationException.Unexpected(s"Impossible to accept simulation ${rejected.id}!", cause.toThrowable)
    }
  }

  private def acceptedHeader = ResponseHeader(202, MessageProtocol.empty, Nil)
}
