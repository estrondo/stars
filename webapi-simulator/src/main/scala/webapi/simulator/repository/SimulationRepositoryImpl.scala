package webapi.simulator.repository

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.util.Timeout
import akkahelper.ShardActorRef
import com.typesafe.scalalogging.StrictLogging
import util._
import webapi.core.repository.SimulationRepository
import webapi.core.{Simulation, WebApiException}
import webapi.simulator.entity.SimulationEntity

import scala.concurrent.{ExecutionContext, Future}

class SimulationRepositoryImpl(
    region: ShardActorRef[SimulationEntity.Command]
)(implicit timeout: Timeout, system: ActorSystem[_])
    extends SimulationRepository
    with StrictLogging {

  override def allocate(id: String): Future[Simulation] = {
    logger.debug("Trying to allocate new simulation {}.", id)

    val reply = region
      .ask[Option[Simulation]](replyTo => ShardingEnvelope(id, SimulationEntity.AllocateCommand(id, replyTo)))
      .throwBy(
        new WebApiException.Unexpected(
          s"When trying to allocate simulation $id!",
          _
        )
      )

    for (option <- reply) yield option match {
      case Some(simulation) =>
        logger.debug("Simulation {} has been allocated.", id)
        simulation

      case None =>
        logger.debug("Simulation {} has been rejected.", id)
        throw new WebApiException.Rejected(s"There already exists Simulation $id!")
    }
  }

  private implicit def executor: ExecutionContext = system.executionContext
}
