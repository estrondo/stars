package stars.simulator

import akka.Done
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorSystem, Behavior}
import com.typesafe.scalalogging.StrictLogging

object MainActor extends App {

  ActorSystem(behavior, "stars-simulator")

  def behavior: Behavior[Done] = Behaviors.setup { context =>
    new MainActor(context).start
  }
}

class MainActor(context: ActorContext[Done]) extends StrictLogging {

  logger.debug("Starting.")

  val start: Behavior[Done] = Behaviors.empty
}
