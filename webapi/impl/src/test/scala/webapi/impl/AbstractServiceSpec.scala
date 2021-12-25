package webapi.impl

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.testkit.scaladsl.TestEntityRef
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LocalServiceLocator}
import com.lightbend.lagom.scaladsl.testkit.{ServiceTest, TestTopicComponents}
import play.api.libs.ws.ahc.AhcWSComponents
import testkit.addSystemProperty
import webapi.SimulationService
import webapi.impl.component.{RequiresCommandRegionComponent, WebAPIComponent}
import webapi.impl.simulator.{Command, SimulatorEntity}

abstract class AbstractServiceSpec extends AsyncSpec {

  lazy val server = ServiceTest.startServer(ServiceTest.defaultSetup) { ctx =>
    new LagomApplication(ctx)
      with WebAPIComponent
      with AhcWSComponents
      with LocalServiceLocator
      with TestTopicComponents
      with RequiresCommandRegionComponent {

      override def commandRegion: ActorRef[ShardingEnvelope[Command]] = {
        testKit.spawn[ShardingEnvelope[Command]](Behaviors.receiveMessage { message =>
          simulatorEntityRefTest.tell(message.message)
          Behaviors.same
        })
      }
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
    addSystemProperty(
      "STARS_WEBAPI_TIMEOUT" -> "5s",
      "DB_DEFAULT_PASSWORD" -> "PASSWORD",
      "DB_DEFAULT_URL" -> "URL",
      "DB_DEFAULT_USERNAME" -> "USER",
      "KAFKA_CONSUMER_PORT" -> "9090",
      "KAFKA_PRODUCER_PORT" -> "9090",
      "KAFKA_CONSUMER_HOST" -> "localhost",
      "KAFKA_PRODUCER_HOST" -> "localhost"
    )

    super.beforeAll()
  }
}
