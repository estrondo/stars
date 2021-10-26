package stars.webapi.impl.persistence

import akka.cluster.sharding.typed.scaladsl.Entity
import com.lightbend.lagom.scaladsl.persistence.slick.SlickPersistenceComponents
import com.lightbend.lagom.scaladsl.server.LagomServerComponents

trait SimulationStoreComponents
  extends LagomServerComponents
    with SlickPersistenceComponents {

  clusterSharding.init(Entity(SimulationStore.typeKey) { ctx =>
    SimulationStore(ctx)
  })
}
