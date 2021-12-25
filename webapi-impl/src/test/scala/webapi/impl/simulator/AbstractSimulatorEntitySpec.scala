package webapi.impl.simulator

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, TestProbe}
import akka.persistence.testkit.PersistenceTestKitPlugin
import akka.persistence.testkit.scaladsl.EventSourcedBehaviorTestKit
import com.typesafe.config.ConfigFactory
import org.scalatest.OptionValues
import simulation.protocol.SimulationCommand
import webapi.impl.Spec

import java.util.UUID

abstract class AbstractSimulatorEntitySpec extends Spec with OptionValues {

  val simulatorEntityConfig = ConfigFactory.parseString(
    """
      |akka.actor.serialization-bindings."webapi.impl.simulator.Command$Create" = jackson-cbor
      |akka.actor.serialization-bindings."webapi.impl.simulator.State$Empty$" = jackson-cbor
      |akka.actor.serialization-bindings."webapi.impl.simulator.Command$CreateResponse" = jackson-cbor
      |akka.actor.serialization-bindings."webapi.impl.simulator.Event$Created" = jackson-cbor
      |akka.actor.serialization-bindings."webapi.impl.simulator.State$Sent" = jackson-cbor
      |""".stripMargin
  )

  val testKit = ActorTestKit(PersistenceTestKitPlugin.config.withFallback(EventSourcedBehaviorTestKit.config)
    .withFallback(simulatorEntityConfig))

  def newEntityRef(): (EventSourcedBehaviorTestKit[Command, Event, State], TestProbe[SimulationCommand], String) = {
    val newId = UUID.randomUUID().toString
    val probe = testKit.createTestProbe[SimulationCommand]()
    (EventSourcedBehaviorTestKit(testKit.system, SimulatorEntity.create(newId, probe.ref)), probe, newId)
  }

  override protected def afterAll(): Unit = {
    testKit.shutdownTestKit()
    super.afterAll()
  }
}
