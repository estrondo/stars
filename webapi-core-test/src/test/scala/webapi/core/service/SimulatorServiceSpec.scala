package webapi.core.service

import org.scalamock.matchers.ArgCapture.CaptureOne
import webapi.core.fixture.{SimulationDescriptionFixture, SimulatorServiceFixture}
import webapi.core.gateway.SimulatorGateway
import webapi.core.repository.SimulationRepository
import webapi.core.service.impl.SimulatorServiceImpl
import webapi.core.{AsyncSpec, Simulation, SimulationReceipt, WebApiException}

import scala.concurrent.Future

class SimulatorServiceSpec extends AsyncSpec {

  "A SimulatorService" - {

    "when receive a valid simulation" - {

      "should send it to SimulatorGateway" in {
        val gateway = mock[SimulatorGateway]
        val repository = mock[SimulationRepository]
        val newID = CaptureOne[String]()

        (repository.allocate _).expects(capture(newID)).onCall { x: String =>
          Future.successful(Simulation(x, Simulation.Empty))
        }

        (gateway.send _).expects(where { (v, _) => v == newID.value }).onCall { (id, description) =>
          Future.successful(SimulationReceipt(id, description))
        }

        val service = new SimulatorServiceImpl(repository, gateway, SimulatorServiceFixture.newConfiguration())
        val receipt = service.register(SimulationDescriptionFixture.newDescription())

        for (receipt <- receipt) yield {
          receipt.id should be(newID.value)
        }
      }

      "and if it's necessary resend a repeated simulation id" in {
        val gateway = mock[SimulatorGateway]
        val repository = mock[SimulationRepository]
        val newID = CaptureOne[String]()

        (repository.allocate _).expects(*).returning(Future.failed(new WebApiException.AlreadyExist("!!!")))
        (repository.allocate _).expects(capture(newID)).onCall { x: String =>
          Future.successful(Simulation(x, Simulation.Empty))
        }

        val service = new SimulatorServiceImpl(repository, gateway, SimulatorServiceFixture.newConfiguration())
        val description = SimulationDescriptionFixture.newDescription()
        val receipt = service.register(description)

        (gateway.send _).expects(where { (x, _) => x == newID.value }).onCall { (id, description) =>
          Future.successful(SimulationReceipt(id, description))
        }

        for (receipt <- receipt) yield {
          receipt.id should be(newID.value)
          receipt.description should be(description)
        }
      }
    }
  }
}
