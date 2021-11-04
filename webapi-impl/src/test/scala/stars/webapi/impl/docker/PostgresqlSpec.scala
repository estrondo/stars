package stars.webapi.impl.docker

import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import org.scalatest.{BeforeAndAfterAll, Suite}
import org.testcontainers.utility.DockerImageName

trait PostgresqlSpec extends ForAllTestContainer with BeforeAndAfterAll {
  this: Suite =>

  val postgresContainer: PostgreSQLContainer = PostgreSQLContainer(dockerImageNameOverride = DockerImageName.parse("postgres:11-alpine"))

  override protected def beforeAll(): Unit = {
    // I really don't like this, but it works!
    System.setProperty("DB_DEFAULT_URL", postgresContainer.container.getJdbcUrl)
    System.setProperty("DB_DEFAULT_PASSWORD", postgresContainer.container.getPassword)
    System.setProperty("DB_DEFAULT_USERNAME", postgresContainer.container.getUsername)
    super.beforeAll()
  }
}
