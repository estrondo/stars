package webapi.impl.simulator

import akka.actor.typed.{ActorRef, Behavior}
import akka.cluster.sharding.typed.scaladsl.{EntityContext, EntityTypeKey}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.EventSourcedBehavior
import com.lightbend.lagom.scaladsl.persistence.AkkaTaggerAdapter
import com.typesafe.scalalogging.StrictLogging
import simulator.protocol.{CreateSimulation, Simulation, SimulationCommand}
import webapi.impl.protocol.Attempt
import webapi.impl.simulator.SimulatorEntity._

object SimulatorEntity {

  type Effect = akka.persistence.typed.scaladsl.Effect[Event, State]

  type CommandHandler = (State, Command) => Effect

  type EventHandler = (State, Event) => State

  //noinspection TypeAnnotation
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
      if (simulation.id == id) {
        newSimulation(simulation, replyTo)
      } else {
        logger.warn("Unexpected new simulation {}!", simulation.id)
        val message = Command.CreateResponse(Attempt.Failure(simulation, new IllegalArgumentException("Illegal ID!")))

        Effect.reply(replyTo)(message)
      }

    case (State.Sent(simulation), Command.Accepted(acceptedID)) =>
      if (acceptedID == id) {
        accept(simulation)
      } else {
        logger.warn("Unexpected accepted simulation {}!", acceptedID)
        Effect.none
      }

    case _ =>
      Effect.unhandled
  }

  val eventHandler: EventHandler = {
    case (State.Empty, Event.Created(simulation)) =>
      onCreated(simulation)

    case (State.Sent(simulation), Event.Accepted(_)) =>
      onAccepted(simulation)
  }

  private def accept(simulation: Simulation): Effect = {
    Effect
      .persist(Event.Accepted(simulation))
      .thenNoReply()
  }

  private def newSimulation(simulation: Simulation, replyTo: ActorRef[Command.CreateResponse]): Effect = {
    Effect
      .persist(Event.Created(simulation))
      .thenReply(replyTo) {
        case State.Sent(simulation) => Command.CreateResponse(Attempt.Success(simulation))
      }
  }

  private def onAccepted(simulation: Simulation): State = {
    logger.debug("Simulation {} was accepted by simulator.", simulation.id)
    State.Processing(simulation)
  }

  private def onCreated(simulation: Simulation): State = {
    dispatcher ! CreateSimulation(Some(simulation))
    State.Sent(simulation)
  }

}
