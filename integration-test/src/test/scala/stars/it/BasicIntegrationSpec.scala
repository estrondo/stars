package stars.it

import testkit.fixture.webapi.CreateSimulationFixture
import sttp.client3._
import sttp.client3.playJson._
import webapi.protocol.CreateSimulationResponse

class BasicIntegrationSpec extends DockerEnvSpec {

  override val envCode: String = "01"

  "The WebAPI" - {
    "when receive a valid simulation order" - {
      "should to put it in simulator stack." in {
        val command = CreateSimulationFixture
          .newCreateSimulation()

        val request = basicRequest
          .body(command)
          .response(asJson[CreateSimulationResponse])
          .post(uri"http://localhost:$webAPIPort/simulate")

        val response = sttpBackend.send(request)
        response.statusText should be("Accepted")
        val Right(CreateSimulationResponse(_, _)) = response.body


      }
    }
  }

}
