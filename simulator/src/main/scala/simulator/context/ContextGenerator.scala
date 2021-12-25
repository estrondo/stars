package simulator.context

import akka.actor.typed.receptionist.ServiceKey
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import com.typesafe.scalalogging.StrictLogging
import simulation.protocol.Simulation
import simulator.context.ContextGenerator.{Behavior, Generate, Message}


object ContextGenerator {

  private type Behavior = akka.actor.typed.Behavior[Message]

  case class Generate(simulation: Simulation) extends Message

  val Key: ServiceKey[Message] = ServiceKey("stars-context-generator")

  def apply(): Behavior = Behaviors.setup { ctx =>
    new ContextGenerator(ctx).initialBehavior
  }

  sealed trait Message
}

/**
 * It's responsible to populate with initial stars and others stuffs.
 */
class ContextGenerator(context: ActorContext[Message]) extends StrictLogging {

  private val initialBehavior: Behavior = Behaviors.receiveMessagePartial {
    case Generate(simulation) => generate(simulation)
  }

  private def generate(simulation: Simulation): Behavior = {
    logger.debug("Generating simulation [{}].", simulation.id)
    // TODO: Implement!
    initialBehavior
  }
}
