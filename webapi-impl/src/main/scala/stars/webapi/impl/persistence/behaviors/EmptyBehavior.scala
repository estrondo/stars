package stars.webapi.impl.persistence.behaviors

import akka.persistence.typed.scaladsl.Effect
import stars.webapi.impl.persistence.SimulationCommand.Create
import stars.webapi.impl.persistence.SimulationEvent.Created
import stars.webapi.impl.persistence.{SimulationCommand, SimulationEvent, SimulationState}
import stars.webapi.impl.persistence.SimulationState.{Empty, RE, Waiting}

trait EmptyBehavior extends SimulationStateBehavior {

  override def apply(command: SimulationCommand): RE = command match {
    case Create(order, replyTo) =>
      Effect.persist(Created(order)).thenReply(replyTo) {
        case Waiting(storedOrder) => Right(storedOrder)
        case _ => Left(order -> new IllegalStateException())
      }
  }

  override def apply(event: SimulationEvent): SimulationState = event match {
    case Created(order) => Waiting(order)
    case _ => Empty
  }
}
