package stars.simulator.kafka

import akka.kafka.ProducerSettings
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import org.scalatest.Suite
import org.testcontainers.containers.DockerComposeContainer
import stars.simulator.Spec
import stars.testkit.docker.DockerComposeForAllSpec
import stars.textkit.addSystemProperty

import java.io.File

object KafkaSpec {

  def prepareEnv(port: Int): Int = {
    System.getProperties.put("STARS_KAFKA_CONSUMER_SERVICE_NAME", "kafka")
    System.getProperties.put("STARS_CLERK_TIMEOUT", "5s")
    port
  }
}

trait KafkaSpec extends DockerComposeForAllSpec {
  this: Spec =>

  override protected def beforeAll(): Unit = {
    val port = container.getServicePort("kafka", 9092).toString
    addSystemProperty(
      "KAFKA_CONSUMER_PORT" -> port,
      "KAFKA_PRODUCER_PORT" -> port,
      "KAFKA_CONSUMER_HOST" -> "localhost",
      "KAFKA_PRODUCER_HOST" -> "localhost"
    )
    super.beforeAll()
  }

  override protected def configure(container: DockerComposeContainer[_]): Unit = {
    container.withExposedService("kafka", 9092)
  }

  override protected def dockerComposeFile: File =
    new File("env/simple/docker-compose.yml".replace('/', File.separatorChar))

  protected def producerSettings(path: String): ProducerSettings[String, Array[Byte]] = {
    val config = this.config.getConfig(path)

    ProducerSettings(config, new StringSerializer, new ByteArraySerializer)
      .withBootstrapServers(s"localhost:${container.getServicePort("kafka", 9092)}")
  }
}
