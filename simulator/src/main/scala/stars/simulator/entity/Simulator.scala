package stars.simulator.entity

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.{Entity, EntityTypeKey}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.EventSourcedBehavior
import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import stars.simulation.protocol.{NewSimulation, SimulationCommand}
import stars.simulator.entity.Simulator.{CommandHandler, Configuration, Effect, EventHandler}
import stars.simulator.entity.State.Empty
import stars.simulator.util.MakeString

object Simulator {

  import akka.persistence.typed.scaladsl

  type Effect = scaladsl.Effect[Event, State]

  type CommandHandler = (State, SimulationCommand) => Effect

  type EventHandler = (State, Event) => State

  class Configuration(val validator: Validator)


  val TypeKey = EntityTypeKey[SimulationCommand]("SimulatorEntity")

  val Effect = scaladsl.Effect

  def apply(id: String, configuration: Configuration): Behavior[SimulationCommand] =
    Behaviors.setup { context =>
      val simulator = new Simulator(id, context, configuration)
      EventSourcedBehavior[SimulationCommand, Event, State](
        persistenceId = PersistenceId(TypeKey.name, id),
        emptyState = State.Empty(id),
        commandHandler = simulator.commandHandler,
        eventHandler = simulator.eventHandler
      ).snapshotWhen(simulator.snapshotWhen)
    }

  def createEntity(config: Config): Entity[SimulationCommand, ShardingEnvelope[SimulationCommand]] = {
    val configuration = new Configuration(Validator(config.getConfig("validator")))

    Entity(TypeKey) { entityContext =>
      apply(entityContext.entityId, configuration)
    }
  }
}


class Simulator(
  id: String,
  context: ActorContext[SimulationCommand],
  configuration: Configuration
) extends StrictLogging {

  logger.debug("Waking up for simulation [{}].", id)

  val commandHandler: CommandHandler = {
    case (_: Empty, command: NewSimulation) if command.id == id =>
      newSimulation(command)

    case ignoring =>
      logger.warn("Ignoring command: {}.", ignoring)
      Effect.none
  }

  val eventHandler: EventHandler = {
    case (state: Empty, Event.Accepted(_, description)) =>
      logger.debug("Creating context for simulation [{}].", id)
      state
    case (state, ignoring) =>
      logger.warn("Ignoring event: {}.", ignoring)
      state
  }

  val snapshotWhen: (State, Event, Long) => Boolean = (_, _, _) => false

  private def newSimulation(command: NewSimulation): Effect = command.description match {
    case Some(description) =>
      logger.debug("Validating simulation [{}].", id)
      configuration.validator(description) match {
        case Left(errors) =>
          logger.warn("Simulation [{}] is invalid, it'll be rejected: {}.", command.id, new MakeString(errors))
          Effect.none

        case Right(_) =>
          logger.info("Received a new simulation [{}].", id)
          Effect.persist(Event.Accepted(id, description))
      }
  }
}