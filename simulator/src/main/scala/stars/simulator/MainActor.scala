package stars.simulator

import akka.Done
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorSystem, Behavior}
import com.typesafe.scalalogging.StrictLogging
import stars.simulator.entity.SimulatorStarter

object MainActor extends App with StrictLogging {

  ActorSystem(behavior, "stars-simulator")

  def behavior: Behavior[Done] = Behaviors.setup { context =>
    new MainActor(context).behavior
  }
}

class MainActor(context: ActorContext[Done]) extends StrictLogging {

  logger.debug("Starting.")

  SimulatorStarter(context)


  private val behavior: Behavior[Done] = Behaviors.empty
}
