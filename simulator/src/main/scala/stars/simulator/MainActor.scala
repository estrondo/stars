package stars.simulator

import akka.Done
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorSystem, Behavior}
import akka.stream.Materializer
import com.typesafe.scalalogging.StrictLogging
import stars.simulator.entity.SimulatorShard

object MainActor extends App with StrictLogging {

  ActorSystem(behavior, "stars-simulator")

  def behavior: Behavior[Done] = Behaviors.setup { context =>
    new MainActor(context).behavior
  }
}

class MainActor(context: ActorContext[Done]) extends StrictLogging {

  logger.debug("Starting.")

  private implicit val materializer = Materializer(context)

  SimulatorShard(context.system)


  private val behavior: Behavior[Done] = Behaviors.empty
}
