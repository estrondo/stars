package stars.webapi.impl.persistence

import akka.cluster.sharding.typed.scaladsl.Entity
import com.lightbend.lagom.scaladsl.persistence.ReadSidePersistenceComponents
import com.lightbend.lagom.scaladsl.persistence.slick.SlickPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.softwaremill.macwire.wire
import stars.webapi.impl.database.{Migration, SimulationRepository}
import stars.webapi.impl.infra.SimulationJsonSerializerRegistry

import scala.concurrent.ExecutionContextExecutor

trait SimulationPersistenceComponents {
  this: ReadSidePersistenceComponents with SlickPersistenceComponents =>


  override def jsonSerializerRegistry: JsonSerializerRegistry = SimulationJsonSerializerRegistry

  def simulationRepository: SimulationRepository

  private implicit def executor: ExecutionContextExecutor = actorSystem.dispatcher

  clusterSharding.init(Entity(SimulationPersistence.typeKey) { ctx =>
    SimulationPersistence(ctx)
  })

  readSide.register(wire[SimulationReadSideProcessor])

  Migration(db)
}
