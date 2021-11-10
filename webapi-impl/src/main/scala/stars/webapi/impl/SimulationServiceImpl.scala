package stars.webapi.impl

import akka.cluster.sharding.typed.scaladsl.EntityRef
import akka.util.Timeout
import com.lightbend.lagom.scaladsl.api.transport.{MessageProtocol, ResponseHeader}
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import com.typesafe.scalalogging.StrictLogging
import stars.webapi.impl.mapper.{SimulationToWebAPI, WebAPIToSimulation}
import stars.webapi.impl.simulator.Command
import stars.webapi.protocol.CreateSimulationResponse
import stars.webapi.{SimulationException, SimulationService}

import java.util.UUID
import scala.concurrent.ExecutionContextExecutor

class SimulationServiceImpl(
  getEntity: UUID => EntityRef[Command]
)(implicit executor: ExecutionContextExecutor, timeout: Timeout) extends SimulationService with StrictLogging {

  private val AcceptedStatus = ResponseHeader(202, MessageProtocol.empty, Nil)

  //noinspection TypeAnnotation
  override def simulate = ServerServiceCall { (_, createSimulation) =>

    val id = UUID.randomUUID()
    logger.debug("Receive a new simulation, it will have id {}.", id)

    val command = WebAPIToSimulation.newSimulation(id.toString, createSimulation)
    for (Command.NewResponse(simulation, error) <- getEntity(id).ask(Command.New(command, _))) yield error match {
      case None =>
        logger.debug("Simulation {} was accepted.", id)
        AcceptedStatus -> CreateSimulationResponse(id, SimulationToWebAPI.createSimulation(simulation.description))

      case Some(cause) =>
        logger.warn("An error on simulation {}!", id, cause)
        throw SimulationException.Unexpected("Impossible to accept!", cause)
    }
  }
}
