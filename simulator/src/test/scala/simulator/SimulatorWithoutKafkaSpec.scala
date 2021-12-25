package simulator

import akka.persistence.testkit.scaladsl.EventSourcedBehaviorTestKit
import simulator.fixture.ValidatorFixture
import simulation.protocol.CreateSimulation
import simulator.context.ContextGenerator
import simulator.entity.{Event, Message, Simulator, State}
import testkit.fixture.simulation.SimulationFixture
import textkit.newRandomId

class SimulatorWithoutKafkaSpec extends SimulatorWithoutKafkaSpecLike {

  "Simulation when" - {

    "receive a valid new simulation" - {
      "should to trigger accepted event" in {
        val id = newRandomId()
        val simulation = SimulationFixture.newNewSimulation(id)
        val generatorProbe = testKit.createTestProbe[ContextGenerator.Message]()

        val validator = ValidatorFixture.newValidator()
        val eventSource = EventSourcedBehaviorTestKit[Message, Event, State](
          system = system,
          behavior = Simulator(id, new Simulator.Configuration(validator, generatorProbe.ref))
        )

        eventSource.runCommand(Message.Command(CreateSimulation(Some(simulation))))
          .event should be(Event.Accepted(simulation))
        val generatorMessage = generatorProbe.expectMessageType[ContextGenerator.Generate]
        generatorMessage.simulation should be(simulation)
      }
    }
  }
}
