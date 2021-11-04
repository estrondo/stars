package stars.simulator

import akka.Done
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorSystem, Behavior}
import com.typesafe.scalalogging.StrictLogging

object MainActor extends App with StrictLogging {

  ActorSystem(behavior, "stars-simulator")

  def behavior: Behavior[Done] = Behaviors.setup { context =>
    new MainActor(context).start
  }
}

class MainActor(context: ActorContext[Done]) extends StrictLogging {

  logger.debug("Starting.")

  private val start: Behavior[Done] = Behaviors.empty
}
