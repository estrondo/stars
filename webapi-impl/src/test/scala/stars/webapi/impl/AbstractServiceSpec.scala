package stars.webapi.impl

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.cluster.sharding.typed.scaladsl.EntityRef
import akka.cluster.sharding.typed.testkit.scaladsl.TestEntityRef
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LocalServiceLocator}
import com.lightbend.lagom.scaladsl.testkit.{ServiceTest, TestTopicComponents}
import play.api.libs.ws.ahc.AhcWSComponents
import stars.webapi.SimulationService
import stars.webapi.impl.infra.{SimulatorComponentRequired, WebAPIComponent}
import stars.webapi.impl.simulator.{Command, SimulatorEntity}

import java.util.UUID

abstract class AbstractServiceSpec extends AsyncSpec {

  lazy val server = ServiceTest.startServer(ServiceTest.defaultSetup) { ctx =>
    new LagomApplication(ctx)
      with WebAPIComponent
      with AhcWSComponents
      with LocalServiceLocator
      with TestTopicComponents
      with SimulatorComponentRequired {

      override def getSimulatorEntityRef: UUID => EntityRef[Command] = _ => simulatorEntityRefTest
    }
  }

  lazy val testKit = ActorTestKit()

  lazy val simulatorEntityRefProbe = testKit.createTestProbe[Command]()

  lazy val simulatorEntityRefTest = TestEntityRef(SimulatorEntity.TypeKey, "----", simulatorEntityRefProbe.ref)

  lazy val client = server.serviceClient.implement[SimulationService]

  //noinspection TypeAnnotation
  def application = server.application

  implicit def materializer = server.materializer

  implicit def system = server.actorSystem

  override protected def afterAll(): Unit = {
    super.afterAll()
    server.stop()
    testKit.shutdownTestKit()
  }

  override protected def beforeAll(): Unit = {
    Map(
      "STARS_WEBAPI_TIMEOUT" -> "5s",
      "DB_DEFAULT_PASSWORD" -> "PASSWORD",
      "DB_DEFAULT_URL" -> "URL",
      "DB_DEFAULT_USERNAME" -> "USER"
    ).foreach {
      case (k, v) => System.setProperty(k, v)
    }

    super.beforeAll()
  }
}
