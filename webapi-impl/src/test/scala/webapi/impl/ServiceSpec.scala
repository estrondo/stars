package webapi.impl

import com.lightbend.lagom.scaladsl.api.transport.TransportException
import testkit.fixture.webapi.CreateSimulationFixture
import webapi.impl.simulator.Command

import scala.util.Failure

class ServiceSpec extends AbstractServiceSpec {

  "POST /simulate" - {

    "with a valid simulation" - {
      "should to respond with accepted(202) status" in {
        val userCommand = CreateSimulationFixture.newCreateSimulation()
        val response = client.simulate.withResponseHeader.invoke(userCommand)

        val Command.Create(newSimulation, replyTo) = simulatorEntityRefProbe.expectMessageType[Command.Create]
        replyTo ! Command.CreateResponse(newSimulation)

        for ((header, _) <- response) yield {
          header.status should be(202)
        }
      }

      "should to respond with error" in {
        val userCommand = CreateSimulationFixture.newCreateSimulation()
        val response = client.simulate.withResponseHeader.invoke(userCommand)

        val Command.Create(newSimulation, replyTo) = simulatorEntityRefProbe.expectMessageType[Command.Create]
        replyTo ! Command.CreateResponse(newSimulation, Some(new RuntimeException("!!!")))

        for (value <- response.toTry()) yield value match {
          case Failure(cause: TransportException) =>
            cause.errorCode.http should be(500)
            cause.exceptionMessage.detail should be("Impossible to accept!")
        }
      }
    }
  }
}