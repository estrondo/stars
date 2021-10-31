package stars.webapi.impl

import akka.Done
import akka.actor.ActorSystem
import akka.persistence.query.NoOffset
import akka.stream.Materializer
import com.dimafeng.testcontainers.Container
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.{ReadSideTestDriver, ServiceTest, TestTopicComponents}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.freespec.AsyncFreeSpecLike
import org.scalatest.matchers.should.Matchers
import stars.webapi.SimulationService
import stars.webapi.impl.docker.PostgresqlSpec
import stars.webapi.impl.infra.Application
import stars.webapi.impl.persistence.SimulationEvent

import java.util.UUID
import scala.concurrent.Future

abstract class AbstractServiceSpec extends AsyncFreeSpecLike with BeforeAndAfterAll with Matchers with PostgresqlSpec {

  override val container: Container = postgresContainer

  lazy val server = ServiceTest.startServer(ServiceTest.defaultSetup.withJdbc()) { ctx =>
    new Application(ctx) with LocalServiceLocator with TestTopicComponents {
      override lazy val readSide: ReadSideTestDriver = new ReadSideTestDriver
    }
  }

  lazy val client = server.serviceClient.implement[SimulationService]

  lazy val source = client.simulationOrderTopic.subscribe.atMostOnceSource

  //noinspection TypeAnnotation
  def application = server.application

  def feed(event: SimulationEvent): Future[Done] = {
    server.application.readSide.feed(UUID.randomUUID().toString, event, NoOffset)
  }

  implicit def materializer: Materializer = server.materializer

  implicit def system: ActorSystem = server.actorSystem

  override protected def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }
}
