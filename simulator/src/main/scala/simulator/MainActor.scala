package simulator

import akka.Done
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorSystem, Behavior}
import akka.stream.Materializer
import com.typesafe.scalalogging.StrictLogging
import simulator.context.ContextGeneratorStarter
import simulator.entity.SimulatorStarter

object MainActor extends App with StrictLogging {

  ActorSystem(behavior, "stars-simulator")

  def behavior: Behavior[Done] = Behaviors.setup { context =>
    new MainActor(context).behavior
  }
}

class MainActor(context: ActorContext[Done]) extends StrictLogging {

  logger.debug("Starting.")

  private implicit val materializer = Materializer(context)

  private val (_, clusteredGenerator) =
    ContextGeneratorStarter(context)

  private val simulatorShard =
    SimulatorStarter(context.system, clusteredGenerator)


  private val behavior: Behavior[Done] = Behaviors.empty
}
