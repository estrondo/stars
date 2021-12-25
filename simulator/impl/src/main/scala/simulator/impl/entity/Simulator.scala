package simulator.impl.entity

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.{Entity, EntityTypeKey}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.EventSourcedBehavior
import com.typesafe.scalalogging.StrictLogging
import simulator.impl.context.ContextGenerator
import simulator.impl.entity.Message.Command
import simulator.impl.entity.Simulator.{Configuration, Effect, EventHandler, MessageHandler}
import simulator.impl.entity.State.Empty
import simulator.impl.util.MakeString
import simulator.protocol.{CreateSimulation, Simulation}

object Simulator {

  import akka.persistence.typed.scaladsl

  type Effect = scaladsl.Effect[Event, State]

  type MessageHandler = (State, Message) => Effect

  type EventHandler = (State, Event) => State

  class Configuration(
    val validator: Validator,
    val contextGenerator: ActorRef[ContextGenerator.Message]
  )

  val TypeKey = EntityTypeKey[Message]("SimulatorEntity")

  val Effect = scaladsl.Effect

  def apply(id: String, configuration: Configuration): Behavior[Message] =
    Behaviors.setup { context =>
      val simulator = new Simulator(id, context, configuration)
      EventSourcedBehavior[Message, Event, State](
        persistenceId = PersistenceId(TypeKey.name, id),
        emptyState = State.Empty(id),
        commandHandler = simulator.commandHandler,
        eventHandler = simulator.eventHandler
      ).snapshotWhen(simulator.snapshotWhen)
    }

  def createEntity(configuration: Configuration): Entity[Message, ShardingEnvelope[Message]] =
    Entity(TypeKey) { entityContext =>
      apply(entityContext.entityId, configuration)
    }
}


class Simulator(
  id: String,
  context: ActorContext[Message],
  configuration: Configuration
) extends StrictLogging {

  import configuration._

  logger.debug("Waking up for simulation [{}].", id)

  val commandHandler: MessageHandler = {
    case (_: Empty, Command(CreateSimulation(Some(simulation), _))) if simulation.id == id =>
      createSimulation(simulation)

    case ignoring =>
      logger.warn("Ignoring command: {}.", ignoring)
      Effect.none
  }

  val eventHandler: EventHandler = {
    case (state: Empty, Event.Accepted(simulation)) =>
      logger.debug("Sending simulation [{}] to context generator.", id)
      contextGenerator ! ContextGenerator.Generate(simulation)
      state
    case (state, ignoring) =>
      logger.warn("Ignoring event: {}.", ignoring)
      state
  }

  val snapshotWhen: (State, Event, Long) => Boolean = (_, _, _) => false

  private def createSimulation(simulation: Simulation): Effect = {
    logger.debug("Validating simulation [{}].", id)
    validator(simulation) match {
      case Right(_) =>
        logger.info("Received a new simulation [{}].", id)
        Effect
          .persist(Event.Accepted(simulation))
          .thenNoReply()

      case Left(errors) =>
        // TODO: Should it to remove itself?
        logger.warn("Simulation [{}] is invalid, it'll be rejected: {}.", simulation.id, new MakeString(errors))
        Effect.none
    }
  }

}