package stars.webapi.impl.database

import org.flywaydb.core.Flyway

import javax.sql.DataSource
import scala.concurrent.{ExecutionContextExecutor, Future}

object Migration {

  def apply(ds: DataSource)(implicit executor: ExecutionContextExecutor): Future[Unit] = {
    Future {
      Flyway
        .configure()
        .dataSource(ds)
        .load()
        .migrate()
    }
  }
}
