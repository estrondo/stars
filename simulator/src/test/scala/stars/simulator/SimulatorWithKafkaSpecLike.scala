package stars.simulator

import org.testcontainers.containers.DockerComposeContainer
import stars.testkit.docker.DockerComposeForAllSpec
import stars.textkit.addSystemProperty

import java.io.File

trait SimulatorWithKafkaSpecLike extends SimulatorWithoutKafkaSpecLike with DockerComposeForAllSpec {

  override protected def beforeAll(): Unit = {
    val port = container.getServicePort("kafka", 9092).toString
    addSystemProperty(
      "KAFKA_PRODUCER_PORT" -> port,
      "KAFKA_CONSUMER_PORT" -> port
    )
    super.beforeAll()
  }

  override protected def configure(container: DockerComposeContainer[_]): Unit = {
    container.withExposedService("kafka", 9092)
  }

  override protected def dockerComposeFile: File = {
    new File("env/kafka/docker-compose.yml".replace('/', File.separatorChar))
  }
}
