package webapi.impl.simulator

import simulation.protocol.CreateSimulation
import testkit.fixture.simulation.SimulationFixture

class SimulatorEntitySpec extends AbstractSimulatorEntitySpec {

  "When new simulation" - {

    "it should to respond correctly" in {
      val (behavior, _, id) = newEntityRef()
      val simulation = SimulationFixture.newNewSimulation(id)

      val response = behavior
        .runCommand(Command.Create(simulation, _))
        .reply

      response.simulation should be(simulation)
      response.error shouldBe empty
    }

    "it should to reject a unexpected simulation" in {
      val (behavior, _, _) = newEntityRef()
      val simulation = SimulationFixture.newNewSimulation()
      val response = behavior
        .runCommand(Command.Create(simulation, _))
        .reply

      response.error.value shouldBe an[IllegalArgumentException]
      response.error.value.getMessage should be("Illegal ID!")
    }

    "it should to dispatch after persisted event" in {
      val (behavior, dispatcherProbe, id) = newEntityRef()
      val simulation = SimulationFixture.newNewSimulation(id)
      behavior
        .runCommand(Command.Create(simulation, _))
        .reply

      val Some(expectedSimulation) = dispatcherProbe.expectMessageType[CreateSimulation].simulation
      expectedSimulation.id should be(id)
    }

    "it should to have state sent after dispatching" in {
      val (behavior, probe, id) = newEntityRef()
      val simulation = SimulationFixture.newNewSimulation(id)
      behavior.runCommand(Command.Create(simulation, _)).reply
      probe.expectMessageType[CreateSimulation]
      behavior.getState() should be(State.Sent(simulation))
    }
  }
}
