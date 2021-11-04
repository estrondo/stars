package stars.webapi.impl.infra

import akka.cluster.sharding.typed.scaladsl.{Entity, EntityRef}
import com.lightbend.lagom.scaladsl.cluster.ClusterComponents
import com.lightbend.lagom.scaladsl.server.LagomServerComponents
import stars.webapi.impl.simulator.{Command, SimulatorEntity}

import java.util.UUID

trait SimulatorComponent extends SimulatorComponentRequired {
  this: ClusterComponents with LagomServerComponents with SimulationDispatcherComponentRequired =>

  lazy val getSimulatorEntityRef: UUID => EntityRef[Command] = {
    clusterSharding.init(Entity(SimulatorEntity.TypeKey) { ctx =>
      SimulatorEntity.create(ctx, simulationDispatcher)
    })

    SimulatorEntity.createGetEntity(clusterSharding)
  }
}