package webapi

import akka.util.Timeout
import akkahelper.ShardActorRef
import com.softwaremill.macwire.wire
import webapi.core.repository.SimulationRepository
import webapi.simulator.entity.SimulationEntity
import webapi.simulator.repository.SimulationRepositoryImpl

import scala.concurrent.duration.DurationInt

trait WithSimulationRepository {
  this: WithActorSystem =>

  lazy val simulationRepository: SimulationRepository = wire[SimulationRepositoryImpl]

  protected def simulationEntityShardRef: ShardActorRef[SimulationEntity.Command]

  protected implicit def timeoutSimulationRepository: Timeout = Timeout(5.seconds)
}
