package webapi.simulator.entity

import akka.actor.typed.ActorRef
import akka.cluster.sharding.typed.scaladsl.EntityTypeKey
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import com.typesafe.scalalogging.StrictLogging
import webapi.core.Simulation
import webapi.simulator.entity.SimulationEntity.{
  AllocateCommand,
  AllocatedEvent,
  AllocatedState,
  Command,
  Effect,
  EmptyState,
  Event,
  State,
  Tag
}

object SimulationEntity {

  case object AllocatedEvent extends Event

  case object AllocatedState extends State

  case object EmptyState extends State

  type Effect = akka.persistence.typed.scaladsl.Effect[Event, State]

  case class AllocateCommand(id: String, replyTo: ActorRef[Option[Simulation]]) extends Command

  case class WrapperState(underlying: Simulation.State) extends State

  // noinspection TypeAnnotation
  val Effect = akka.persistence.typed.scaladsl.Effect

  val Tag = "simulator-entity"

  val TypeKey: EntityTypeKey[Command] = EntityTypeKey("simulator-entity")

  sealed trait State

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
  @JsonSubTypes(
    Array(
      new JsonSubTypes.Type(value = classOf[AllocatedEvent.type], name = "allocated")
    )
  )
  sealed trait Event

  sealed trait Command
}

class SimulationEntity(id: String) extends StrictLogging {

  val commandHandler: (State, Command) => Effect = { case (state, AllocateCommand(id, replyTo)) =>
    allocate(state, id, replyTo)
  }

  val eventHandler: (State, Event) => State = { case (EmptyState, AllocatedEvent) =>
    AllocatedState
  }

  val tagger: Event => Set[String] = {
    val set = Set(Tag)
    _ => set
  }

  private def allocate(state: State, id: String, replyTo: ActorRef[Option[Simulation]]): Effect = {
    if (state == EmptyState && id == this.id) {
      logger.info("Simulation {} has been allocated.", id)
      Effect
        .persist(AllocatedEvent)
        .thenReply(replyTo)(_ => Some(Simulation(id, Simulation.Empty)))
    } else {
      logger.debug("Rejecting simulation {}.", id)
      Effect.reply(replyTo)(None)
    }
  }

}
