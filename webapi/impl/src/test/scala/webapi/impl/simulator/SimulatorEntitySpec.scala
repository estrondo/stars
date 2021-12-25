package webapi.impl.simulator

import simulator.protocol.CreateSimulation
import testkit.fixture.simulation.SimulationFixture
import webapi.impl.protocol.Attempt

class SimulatorEntitySpec extends AbstractSimulatorEntitySpec {

  "When new simulation" - {

    "it should to respond correctly" in {
      val (behavior, _, id) = newEntityRef()
      val simulation = SimulationFixture.newNewSimulation(id)

      val response = behavior
        .runCommand(Command.Create(simulation, _))
        .reply

      response.content should be(Attempt.Success(simulation))
    }

    "it should to reject a unexpected simulation" in {
      val (behavior, _, _) = newEntityRef()
      val simulation = SimulationFixture.newNewSimulation()
      val response = behavior
        .runCommand(Command.Create(simulation, _))
        .reply

      val Attempt.Failure(_, cause) = response.content
      cause.message should be("Illegal ID!")
    }

    "it should to dispatch after persisted event" in {
      val (behavior, dispatcherProbe, id) = newEntityRef()
      val simulation = SimulationFixture.newNewSimulation(id)
      behavior
        .runCommand(Command.Create(simulation, _))
        .reply

      val expectedSimulation = dispatcherProbe.expectMessageType[CreateSimulation].getContent
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
