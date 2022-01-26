package webapi

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akkahelper._
import com.typesafe.scalalogging.StrictLogging
import net.ceedubs.ficus.Ficus._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn
import scala.util.{Failure, Success}

object Boostrap extends App with StrictLogging {

  private implicit val system: ActorSystem[_] = ActorSystem(Behaviors.empty, "stars-webapi")

  private val binding = {
    var routes = List(DefaultRoute(system).route)

    if (shouldLoadFeature("creation"))
      SimulationRouter(system).route match {
        case Some(route) => routes = route :: routes
        case _           =>
      }

    val route = concat(routes: _*)

    Http(system)
      .newServerAt(
        system.config.getAs[String]("webapi.server.interface").getOrElse("0.0.0.0"),
        system.config.getAs[Int]("webapi.server.port").getOrElse(8080)
      )
      .bind(route)
  }

  binding.onComplete {
    case Success(value) =>
      logger.info("⚝ Stars WebAPI listening to {} @ {}.", value.localAddress.getHostName, value.localAddress.getPort)
      StdIn.readLine()
      value.unbind() andThen {
        case Success(_) =>
          terminate(0)
        case Failure(cause) =>
          logger.error("\uD83E\uDD97 Something strange happened when server was stopping!", cause)
          terminate(2)
      }
    case Failure(cause) =>
      logger.error("\uD83E\uDD97 Something strange happened when server was starting!", cause)
      terminate(1)
  }

  private def shouldLoadFeature(name: String): Boolean = {
    system.config.getAs[Boolean](s"webapi.feature.$name").getOrElse(true)
  }

  private def terminate(status: Int): Unit = {
    system.terminate()
    system.whenTerminated andThen { _ =>
      logger.debug("ActorSystem has stopped.")
      System.exit(status)
    }
  }
}
