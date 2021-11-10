package stars.webapi.impl.simulator

import stars.simulation.protocol.ToSimulation
import stars.webapi.impl.fixture.SimulationFixture

class SimulatorEntitySpec extends AbstractSimulatorEntitySpec {

  "When new simulation" - {

    "it should to respond correctly" in {
      val (behavior, _, id) = newEntityRef()
      val newSimulation = SimulationFixture.newNewSimulation(id)

      val response = behavior
        .runCommand(Command.New(newSimulation, _))
        .reply

      response.simulation should be(ToSimulation(newSimulation))
      response.error shouldBe empty
    }

    "it should reject a unexpected simulation" in {
      val (behavior, _, _) = newEntityRef()
      val simulation = SimulationFixture.newNewSimulation()
      val response = behavior
        .runCommand(Command.New(simulation, _))
        .reply

      response.error.value shouldBe an[IllegalArgumentException]
      response.error.value.getMessage should be("Illegal ID!")
    }
  }
}
