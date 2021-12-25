package simulator

import akka.kafka.{ConsumerSettings, ProducerSettings}
import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}
import org.slf4j.LoggerFactory
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import testkit.docker.DockerComposeForAllSpec
import textkit.addSystemProperty

import java.io.File

trait SimulatorWithKafkaSpecLike
  extends SimulatorWithoutKafkaSpecLike
    with DockerComposeForAllSpec
    with StrictLogging {

  protected val KafkaServiceName = "kafka"

  protected val KafkaServicePort = 9092

  protected lazy val producerSetting =
    ProducerSettings(config.getConfig("stars.kafka.producer"), new StringSerializer, new ByteArraySerializer)
      .withBootstrapServers(s"localhost:$kafkaPort")

  protected lazy val consumerSetting =
    ConsumerSettings(config.getConfig("stars.kafka.consumer"), new StringDeserializer, new ByteArrayDeserializer)
      .withProperty("auto.offset.reset", "earliest")
      .withBootstrapServers(s"localhost:$kafkaPort")

  override protected def beforeAll(): Unit = {
    addSystemProperty(
      "KAFKA_PRODUCER_PORT" -> kafkaPort.toString,
      "KAFKA_CONSUMER_PORT" -> kafkaPort.toString
    )
    super.beforeAll()
  }


  override protected def configure(container: DockerComposeContainer[_]): Unit = {
    container.withExposedService(KafkaServiceName, KafkaServicePort, Wait.forListeningPort())
    container.withLogConsumer("zookeeper", new Slf4jLogConsumer(LoggerFactory.getLogger("docker-compose-zookeeper")))
    container.withLogConsumer(KafkaServiceName, new Slf4jLogConsumer(LoggerFactory.getLogger(s"docker-compose-$KafkaServiceName")))
  }

  override protected def dockerComposeFile: File = {
    new File("env/kafka/docker-compose.yml".replace('/', File.separatorChar))
  }

  protected def kafkaPort = container.getServicePort(KafkaServiceName, KafkaServicePort)
}
