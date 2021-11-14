package stars.webapi.impl.simulator

import stars.fixture.simulation.SimulationFixture
import stars.simulation.protocol.{NewSimulation, Simulation, ToSimulation}

class SimulatorEntitySpec extends AbstractSimulatorEntitySpec {

  "When new simulation" - {

    "it should to respond correctly" in {
      val (behavior, _, id) = newEntityRef()
      val newSimulationCommand = SimulationFixture.newNewSimulation(id)

      val response = behavior
        .runCommand(Command.New(newSimulationCommand, _))
        .reply

      response.simulation should be(ToSimulation(newSimulationCommand))
      response.error shouldBe empty
    }

    "it should to reject a unexpected simulation" in {
      val (behavior, _, _) = newEntityRef()
      val newSimulationCommand = SimulationFixture.newNewSimulation()
      val response = behavior
        .runCommand(Command.New(newSimulationCommand, _))
        .reply

      response.error.value shouldBe an[IllegalArgumentException]
      response.error.value.getMessage should be("Illegal ID!")
    }

    "it should to dispatch after persisted event" in {
      val (behavior, dispatcherProbe, id) = newEntityRef()
      val simulation = SimulationFixture.newNewSimulation(id)
      behavior
        .runCommand(Command.New(simulation, _))
        .reply

      val simulationCommand = dispatcherProbe.expectMessageType[NewSimulation]
      simulationCommand.id should be(id)
    }

    "it should to have state sent after dispatching" in {
      val (behavior, probe, id) = newEntityRef()
      val newSimulationCommand = SimulationFixture.newNewSimulation(id)
      behavior.runCommand(Command.New(newSimulationCommand, _)).reply
      probe.expectMessageType[NewSimulation]
      behavior.getState() should be (State.Sent(Simulation(id, newSimulationCommand.description)))
    }
  }
}
