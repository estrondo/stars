package stars.testkit.docker

import com.dimafeng.testcontainers.{DockerComposeContainer, ForAllTestContainer}
import org.scalatest.{BeforeAndAfterAll, Suite}
import org.testcontainers.containers

import java.io.File

trait DockerComposeForAllSpec extends ForAllTestContainer with BeforeAndAfterAll {
  this: Suite =>

  override lazy val container: DockerComposeContainer = {
    val compose = DockerComposeContainer(dockerComposeFile)
    compose.container.withLocalCompose(false)
    configure(compose.container)
    compose
  }

  protected def configure(container: containers.DockerComposeContainer[_]): Unit

  protected def dockerComposeFile: File
}
