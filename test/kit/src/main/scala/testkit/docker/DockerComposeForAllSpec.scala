package testkit.docker

import com.dimafeng.testcontainers.{DockerComposeContainer, ForAllTestContainer}
import org.scalatest.{BeforeAndAfterAll, Suite}
import org.testcontainers.containers

import java.io.File

trait DockerComposeForAllSpec extends ForAllTestContainer with BeforeAndAfterAll {
  this: Suite =>

  override lazy val container: DockerComposeContainer = {
    val adapter = DockerComposeContainer(dockerComposeFile)
    adapter.container.withLocalCompose(true)
    adapter.container.withOptions("--compatibility")
    configure(adapter.container)
    adapter
  }

  protected def configure(container: containers.DockerComposeContainer[_]): Unit

  protected def dockerComposeFile: File
}
