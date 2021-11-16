package stars.simulator

import akka.persistence.testkit.scaladsl.EventSourcedBehaviorTestKit
import stars.fixture.ValidatorFixture
import stars.simulation.protocol.SimulationCommand
import stars.simulator.entity.{Event, Simulator, State}
import stars.testkit.fixture.simulation.SimulationFixture
import stars.textkit.newRandomId

class SimulatorWithoutKafkaSpec extends SimulatorWithoutKafkaSpecLike {

  "Simulation when" - {

    "receive a valid new simulation" - {
      "should to trigger accepted event" in {
        val id = newRandomId()
        val newSimulation = SimulationFixture.newNewSimulation(id)

        val validator = ValidatorFixture.newValidator()
        val eventSource = EventSourcedBehaviorTestKit[SimulationCommand, Event, State](
          system = system,
          behavior = Simulator(id, new Simulator.Configuration(validator))
        )

        eventSource.runCommand(newSimulation).event should be(Event.Accepted(id, newSimulation.description.get))
      }
    }
  }
}
