package stars.it

import org.slf4j.LoggerFactory
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import stars.testkit.docker.DockerComposeForAllSpec
import sttp.client3.HttpURLConnectionBackend

import java.io.File

trait DockerEnvSpec
  extends Spec
    with DockerComposeForAllSpec {

  lazy val sttpBackend = HttpURLConnectionBackend()

  def envCode: String

  def webAPIPort: Int = container.getServicePort("stars-webapi", 9000)

  override protected def configure(container: DockerComposeContainer[_]): Unit = {
    container.withExposedService("stars-webapi", 9000, Wait.forLogMessage(".*Initiating new cluster.*", 1))

    container.withLogConsumer("stars-webapi",
      new Slf4jLogConsumer(LoggerFactory.getLogger("docker-stars-simulation-webapi")))
    container.withLogConsumer("simulator",
      new Slf4jLogConsumer(LoggerFactory.getLogger("docker-stars-simulator")))
  }

  override protected def dockerComposeFile: File = new File(s"env/$envCode/docker-compose.yml")
}
