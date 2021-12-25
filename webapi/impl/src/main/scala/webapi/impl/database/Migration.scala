package webapi.impl.database

import com.typesafe.scalalogging.StrictLogging
import org.flywaydb.core.Flyway
import webapi.impl.database.API.Database

import java.io.PrintWriter
import java.sql.Connection
import java.util.logging.Logger
import javax.sql.DataSource

object Migration extends StrictLogging {

  def apply(ds: Database): Unit = {
    logger.debug("Applying migration.")

    try {
      Flyway
        .configure()
        .schemas("stars_flyway")
        .dataSource(new DataSource {

          override def getConnection: Connection = ds.source.createConnection()

          override def getConnection(username: String, password: String): Connection = getConnection

          override def getLogWriter: PrintWriter = ???

          override def setLogWriter(out: PrintWriter): Unit = ???

          override def setLoginTimeout(seconds: Int): Unit = ???

          override def getLoginTimeout: Int = ???

          override def getParentLogger: Logger = ???

          override def unwrap[T](iface: Class[T]): T = ???

          override def isWrapperFor(iface: Class[_]): Boolean = ???
        })
        .load()
        .migrate()
      logger.debug("Migration is done.")
    } catch {
      case cause: Exception => throw new IllegalStateException("Migration has failed!", cause)
    }
  }
}
