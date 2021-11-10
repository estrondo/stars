package stars.simulator.kafka

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.kafka.testkit.KafkaTestkitTestcontainersSettings
import akka.kafka.testkit.scaladsl.{ScalatestKafkaSpec, TestcontainersKafkaPerClassLike}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}

import java.util.UUID

object AbstractKafkaSpec {

  def prepareEnv(port: Int): Int = {
    System.getProperties.put("STARS_KAFKA_CONSUMER_SERVICE_NAME", "kafka")
    System.getProperties.put("STARS_CLERK_TIMEOUT", "5s")
    port
  }
}

abstract class AbstractKafkaSpec(port: Int)
  extends ScalatestKafkaSpec(AbstractKafkaSpec.prepareEnv(port))
    with AnyFreeSpecLike
    with BeforeAndAfterAll
    with Matchers
    with ScalaFutures
    with Eventually
    with TestcontainersKafkaPerClassLike {

  //noinspection SpellCheckingInspection
  override val testcontainersSettings: KafkaTestkitTestcontainersSettings =
    KafkaTestkitTestcontainersSettings(system)
      .withNumBrokers(1)

  protected val testKit: ActorTestKit = ActorTestKit()

  implicit val defaultPatience: PatienceConfig =
    PatienceConfig(timeout = Span(2, Seconds), interval = Span(100, Millis))

  protected def this() = this(-1)

  protected override def afterAll(): Unit = {
    super.afterAll()
    testKit.shutdownTestKit()
  }

  protected def newUUID: UUID = UUID.randomUUID()

  protected def newUUIDString: String = newUUID.toString
}
