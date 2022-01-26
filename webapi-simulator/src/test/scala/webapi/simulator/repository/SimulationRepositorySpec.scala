package webapi.simulator.repository

import akka.cluster.sharding.typed.ShardingEnvelope
import util.{IDGenerator, richFuture}
import webapi.core.{Simulation, WebApiException}
import webapi.simulator.entity.{SimulationEntity, SimulationEntityExt}

import scala.annotation.nowarn
import scala.util.Failure

class SimulationRepositorySpec extends AbstractSimulationRepositorySpec {

  "A SimulationRepository" - {

    "should to allocate a new simulation" in {
      val shardProbe = createTestProbe[ShardingEnvelope[SimulationEntity.Command]]()
      val repository = new SimulationRepositoryImpl(shardProbe.ref)
      val id = IDGenerator.generate(16)
      val simulation = repository.allocate(id)

      val ShardingEnvelope(rID, cmd @ SimulationEntity.AllocateCommand(aID, _)) = shardProbe.receiveMessage()

      rID should be(id)
      aID should be(id)

      val entityActor = spawn(SimulationEntityExt.createBehavior(id))
      entityActor ! cmd

      for (simulation <- simulation) yield {
        simulation.id should be(id)
        simulation.state should be(Simulation.Empty)
      }
    }

    "when has been failed to allocate should notify" in {
      val shardProbe = createTestProbe[ShardingEnvelope[SimulationEntity.Command]]()
      val repository = new SimulationRepositoryImpl(shardProbe.ref)
      val id = IDGenerator.generate(32)
      val simulation = repository.allocate(id)

      val ShardingEnvelope(rID, cmd @ SimulationEntity.AllocateCommand(aID, _)) = shardProbe.receiveMessage()
      rID should be(id)
      aID should be(id)

      val entityActor = spawn(SimulationEntityExt.createBehavior(IDGenerator.generate()))
      entityActor ! cmd

      for (simulation <- simulation.asTry) yield {
        @nowarn val Failure(cause) = simulation
        cause shouldBe a[WebApiException.Rejected]
      }
    }
  }
}
