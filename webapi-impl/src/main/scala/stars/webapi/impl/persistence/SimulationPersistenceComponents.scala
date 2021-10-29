package stars.webapi.impl.persistence

import akka.cluster.sharding.typed.scaladsl.Entity
import com.lightbend.lagom.scaladsl.persistence.slick.SlickPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server.LagomServerComponents
import com.softwaremill.macwire.wire
import controllers.AssetsComponents
import play.api.db.HikariCPComponents
import stars.webapi.impl.database.SimulationRepository
import stars.webapi.impl.infra.SimulationJsonSerializerRegistry

import scala.concurrent.ExecutionContextExecutor

trait SimulationPersistenceComponents
  extends LagomServerComponents
    with SlickPersistenceComponents
    with HikariCPComponents
    with AssetsComponents {

  private implicit def executor: ExecutionContextExecutor = actorSystem.dispatcher

  clusterSharding.init(Entity(SimulationPersistence.typeKey) { ctx =>
    SimulationPersistence(ctx)
  })

  readSide.register(wire[SimulationReadSideProcessor])

  override def jsonSerializerRegistry: JsonSerializerRegistry = SimulationJsonSerializerRegistry


  def simulationRepository: SimulationRepository
}
