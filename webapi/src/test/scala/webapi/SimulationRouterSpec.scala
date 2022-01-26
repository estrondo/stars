package webapi

import akka.actor.typed.ActorSystem
import com.softwaremill.macwire.wire
import stars.webapi.client.api.CreationApi
import stars.webapi.client.model.CreateSimulation

import scala.annotation.nowarn
import scala.concurrent.Future

class SimulationRouterSpec extends AbstractSimulationRouterSpec {

  "POST /simulation" - {
    "should create a new simulation" in {
      @nowarn
      val Some(route) = wire[SimulationRouter].route
      newRoute(route) { client =>
        val creationApi = new CreationApi(client)
        for {
          response <- Future(creationApi.createSimulation {
            new CreateSimulation()
              .name("Galileo's galaxy")
              .email("galileo@universe.org")
          })
        } yield {
          response.getId should be("123")
        }
      }
    }
  }

  override implicit lazy val typedActorSystem: ActorSystem[_] = ActorSystem.wrap(system)
}
