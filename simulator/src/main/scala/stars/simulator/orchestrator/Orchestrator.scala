package stars.simulator.orchestrator

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.typed.{ClusterSingleton, SingletonActor}

import java.util.UUID

object Orchestrator {

  case class ShouldStart(id: UUID) extends Command

  def apply(): Behavior[Command] = Behaviors.setup { ctx =>
    ???
  }

  def init(system: ActorSystem[_]): ActorRef[Command] = {
    ClusterSingleton(system)
      .init(SingletonActor(Orchestrator(), "simulation-orchestrator"))
  }

  sealed trait Command
}

class Orchestrator {

}