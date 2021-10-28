package stars.webapi.impl.persistence

import akka.cluster.sharding.typed.scaladsl.Entity
import com.lightbend.lagom.scaladsl.persistence.slick.SlickPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server.LagomServerComponents
import play.api.db.HikariCPComponents
import stars.webapi.impl.infra.SimulationJsonSerializerRegistry

trait SimulationStoreComponents
  extends LagomServerComponents
    with SlickPersistenceComponents
    with HikariCPComponents {


  clusterSharding.init(Entity(SimulationStore.typeKey) { ctx =>
    SimulationStore(ctx)
  })

  override def jsonSerializerRegistry: JsonSerializerRegistry = SimulationJsonSerializerRegistry
}
