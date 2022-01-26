package webapi.simulator.repository

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.persistence.testkit.scaladsl.PersistenceTestKit
import akka.persistence.testkit.{PersistenceTestKitPlugin, PersistenceTestKitSnapshotPlugin}
import webapi.simulator.SpecWithActorSystem

class AbstractSimulationRepositorySpec
    extends SpecWithActorSystem(
      PersistenceTestKitSnapshotPlugin.config
        .withFallback(
          PersistenceTestKitPlugin.config
            .withFallback(ActorTestKit.ApplicationTestConfig)
        )
    ) {

  protected lazy val persistenceTestKit: PersistenceTestKit = PersistenceTestKit(system)

  override protected def beforeAll(): Unit = {
    persistenceTestKit.clearAll()
    super.beforeAll()
  }
}
