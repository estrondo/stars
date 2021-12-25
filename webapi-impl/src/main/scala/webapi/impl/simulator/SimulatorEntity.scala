package webapi.impl.simulator

import akka.actor.typed.{ActorRef, Behavior}
import akka.cluster.sharding.typed.scaladsl.{EntityContext, EntityTypeKey}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.EventSourcedBehavior
import com.lightbend.lagom.scaladsl.persistence.AkkaTaggerAdapter
import com.typesafe.scalalogging.StrictLogging
import simulation.protocol.{CreateSimulation, Simulation, SimulationCommand}
import webapi.impl.simulator.SimulatorEntity._

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
}

class SimulatorEntity(id: String, dispatcher: ActorRef[SimulationCommand]) extends StrictLogging {

  logger.debug("Starting for simulation {}.", id)

  val commandHandler: CommandHandler = {
    case (State.Empty, Command.Create(simulation, replyTo)) =>
      if (simulation.id == id)
        newSimulation(simulation, replyTo)
      else {
        logger.warn("Unexpected new simulation {}!", simulation.id)
        val message = Command.CreateResponse(simulation,
          Some(new IllegalArgumentException("Illegal ID!")))

        Effect.reply(replyTo)(message)
      }

    case _ =>
      Effect.unhandled
  }

  val eventHandler: EventHandler = {
    case (State.Empty, Event.Created(command)) =>
      dispatch(command)
  }

  private def dispatch(simulation: Simulation): State = {
    logger.debug("Sending simulation [{}].", simulation.id)
    dispatcher ! CreateSimulation(Some(simulation))
    State.Sent(simulation)
  }

  private def newSimulation(simulation: Simulation, replyTo: ActorRef[Command.CreateResponse]): Effect = {
    Effect
      .persist(Event.Created(simulation))
      .thenReply(replyTo) {
        case State.Sent(simulation) => Command.CreateResponse(simulation)
      }
  }

}
