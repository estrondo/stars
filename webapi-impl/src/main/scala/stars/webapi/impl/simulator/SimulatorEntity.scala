package stars.webapi.impl.simulator

import akka.actor.typed.{ActorRef, Behavior}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityContext, EntityRef, EntityTypeKey}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.EventSourcedBehavior
import com.lightbend.lagom.scaladsl.persistence.AkkaTaggerAdapter
import com.typesafe.scalalogging.StrictLogging
import stars.simulation.protocol.{ToSimulation, NewSimulation, SimulationCommand}
import stars.webapi.impl.simulator.SimulatorEntity._

import java.util.UUID

object SimulatorEntity {

  type Effect = akka.persistence.typed.scaladsl.Effect[Event, State]

  type CommandHandler = (State, Command) => Effect

  type EventHandler = (State, Event) => State

  val Effect = akka.persistence.typed.scaladsl.Effect

  val TypeKey: EntityTypeKey[Command] = EntityTypeKey[Command]("SimulatorEntity")

  def create(ctx: EntityContext[Command], dispatcher: ActorRef[SimulationCommand]): Behavior[Command] = {
    create(ctx.entityId, dispatcher)
      .withTagger(AkkaTaggerAdapter.fromLagom(ctx, Event.Tag))
  }

  def create(id: String, dispatcher: ActorRef[SimulationCommand]): EventSourcedBehavior[Command, Event, State] = {
    val entity = new SimulatorEntity(id, dispatcher)

    EventSourcedBehavior
      .apply[Command, Event, State](
        persistenceId = PersistenceId(TypeKey.name, id),
        emptyState = State.Empty,
        commandHandler = entity.commandHandler,
        eventHandler = entity.eventHandler
      )
  }

  def createGetEntity(sharding: ClusterSharding): UUID => EntityRef[Command] = {
    uuid => {
      sharding.entityRefFor(TypeKey, uuid.toString)
    }
  }
}

class SimulatorEntity(id: String, dispatcher: ActorRef[SimulationCommand]) extends StrictLogging {

  logger.debug("Starting for simulation {}.", id)

  val commandHandler: CommandHandler = {
    case (State.Empty, Command.New(command, replyTo)) =>
      if (command.id == id)
        newSimulation(command, replyTo)
      else {
        logger.warn("Unexpected new simulation {}!", command.id)
        val message = Command.NewResponse(ToSimulation.fromNewSimulation(command),
          Some(new IllegalArgumentException("Illegal ID!")))

        Effect.reply(replyTo)(message)
      }

    case _ =>
      Effect.unhandled
  }

  val eventHandler: EventHandler = {
    case (State.Empty, Event.New(command)) =>
      dispatch(command)
  }

  private def dispatch(command: NewSimulation): State = {
    logger.debug("Sending simulation {}.", command.id)
    dispatcher ! command
    State.Sent(ToSimulation.fromNewSimulation(command))
  }

  private def newSimulation(command: NewSimulation, replyTo: ActorRef[Command.NewResponse]): Effect = {
    Effect
      .persist(Event.New(command))
      .thenReply(replyTo) {
        case State.Sent(simulation) => Command.NewResponse(simulation)
      }
  }

}
