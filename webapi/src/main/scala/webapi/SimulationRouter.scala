package webapi

import akka.actor.typed.{ActorSystem, Extension, ExtensionId}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.softwaremill.macwire.wire
import webapi.protocol._
import webapi.adapter.SimulatorServiceAdapter

object SimulationRouter extends ExtensionId[SimulationRouter] {

  override def createExtension(system: ActorSystem[_]): SimulationRouter = {
    val adapter: SimulatorServiceAdapter = ???
    wire[SimulationRouter]
  }
}

class SimulationRouter(
    adapter: SimulatorServiceAdapter
) extends Extension {

  def route: Option[Route] = Some {
    concat(createSimulation, retrieveSimulation)
  }

  private def createSimulation = path("simulation") {
    post {
      entity(as[CreateSimulation]) { command =>
        onSuccess(adapter.register(command)) { case (id, _) =>
          complete(StatusCodes.Accepted -> CreateSimulationResponse(id))
        }
      }
    }
  }

  private def retrieveSimulation = path("simulation" / Segment) { simulationID =>
    get {
      ???
    }
  }
}
