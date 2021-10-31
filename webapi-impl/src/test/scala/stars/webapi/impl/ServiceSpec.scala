package stars.webapi.impl

import akka.stream.testkit.scaladsl.TestSink
import stars.webapi.impl.fixture.SingleCreateSimulation
import stars.webapi.impl.persistence.SimulationEvent
import stars.webapi.protocol.{CreateSimulationResponse, SimulationOrder}

class ServiceSpec extends AbstractServiceSpec {

  "When the service receives a simulation order" - {

    "it should reply with accepted." in {
      val command = SingleCreateSimulation.newCreateSimulation
      for ((header, result) <- client.simulate.withResponseHeader.invoke(command)) yield {
        val CreateSimulationResponse(_, _, order) = result
        order should be(command)
        header.status should be(202)
      }
    }

    "it should write on read-side." in {
      val command = SingleCreateSimulation.newCreateSimulation
      for {
        result <- client.simulate.invoke(command)
        _ <- feed(SimulationEvent.Created(SimulationOrder(result.id, result.order)))
        opt <- application.simulationRepository.get(result.id)
      } yield {
        opt shouldBe defined
        opt.get.email should be(command.email)
      }
    }

    "it should publish on topic" in {
      val command = SingleCreateSimulation.newCreateSimulation
      for (result <- client.simulate.invoke(command)) yield {
        source
          .runWith(TestSink.probe[SimulationOrder])
          .request(1)
          .expectNext shouldNot be(SimulationOrder(result.id, command))
      }
    }
  }
}
