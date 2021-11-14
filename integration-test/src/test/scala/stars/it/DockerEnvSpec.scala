package stars.it

import com.dimafeng.testcontainers.{DockerComposeContainer, ForAllTestContainer}
import org.slf4j.LoggerFactory
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import sttp.client3.HttpURLConnectionBackend

import java.io.File

trait DockerEnvSpec
  extends Spec
    with ForAllTestContainer {

  lazy val sttpBackend = HttpURLConnectionBackend()

  override lazy val container: DockerComposeContainer = {
    val compose = DockerComposeContainer(new File(s"env/$envCode/docker-compose.yml"))
    compose.container.withLocalCompose(false)
    compose.container.withExposedService("stars-webapi", 9000, Wait.forLogMessage(".*Initiating new cluster.*", 1))

    compose.container.withLogConsumer("stars-webapi",
      new Slf4jLogConsumer(LoggerFactory.getLogger("docker-stars-simulation-webapi")))
    compose
  }

  def envCode: String

  def webAPIPort: Int = container.getServicePort("stars-webapi", 9000)
}
