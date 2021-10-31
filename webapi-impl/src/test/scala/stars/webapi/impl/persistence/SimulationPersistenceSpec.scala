package stars.webapi.impl.persistence

import akka.persistence.testkit.scaladsl.EventSourcedBehaviorTestKit
import akka.persistence.typed.PersistenceId
import stars.webapi.impl.persistence.SimulationState.Waiting
import stars.webapi.impl.ActorSpecWithConfig
import stars.webapi.impl.fixture.SingleSimulationOrder

class SimulationPersistenceSpec extends ActorSpecWithConfig(EventSourcedBehaviorTestKit.config) {

  private val eventSourceTest = EventSourcedBehaviorTestKit[SimulationCommand, SimulationEvent, SimulationState](
    system,
    SimulationEntityActor(PersistenceId("Simulation", "X"))
  )

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    eventSourceTest.clear()
  }

  "An empty simulation" - {

    "should be created when an order was required" in new SingleSimulationOrder {
      val result = eventSourceTest.runCommand(SimulationCommand.Create(simulationOrder, _))
      result.reply should be(Right(simulationOrder))
      result.event should be(SimulationEvent.Created(simulationOrder))
      result.state should be(Waiting(simulationOrder))
    }
  }

  "A waiting simulation" - {

    "should reject a create command" in new SingleSimulationOrder {
      eventSourceTest.runCommand(SimulationCommand.Create(simulationOrder, _))
      val result = eventSourceTest.runCommand(SimulationCommand.Create(simulationOrder, _))
      result.state should be(Waiting(simulationOrder))
      val Left((order, _)) = result.reply
      order should be(simulationOrder)
    }
  }
}
