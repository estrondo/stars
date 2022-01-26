package webapi.simulator.gateway

import akka.kafka.scaladsl.SendProducer
import akka.kafka.testkit.KafkaTestkitTestcontainersSettings
import akka.kafka.testkit.scaladsl.{ScalatestKafkaSpec, TestcontainersKafkaPerClassLike}
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.freespec.AsyncFreeSpecLike
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

abstract class AbstractSimulatorGatewayKafkaSpec(kafkaPort: Int = -1)
    extends ScalatestKafkaSpec(kafkaPort)
    with AsyncFreeSpecLike
    with Matchers
    with ScalaFutures
    with Eventually
    with TestcontainersKafkaPerClassLike
    with BeforeAndAfterAll {

  override val ec: ExecutionContext = system.dispatcher

  // noinspection SpellCheckingInspection
  override val testcontainersSettings: KafkaTestkitTestcontainersSettings =
    KafkaTestkitTestcontainersSettings(system)
      .withNumBrokers(2)

  protected lazy val sendProducer: SendProducer[String, Array[Byte]] =
    SendProducer(producerDefaults(new StringSerializer(), new ByteArraySerializer()))

  protected override def afterAll(): Unit = {
    Await.result(sendProducer.close(), Duration.Inf)
    super.afterAll()
  }
}
