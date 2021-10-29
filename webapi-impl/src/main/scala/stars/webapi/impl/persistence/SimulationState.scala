package stars.webapi.impl.persistence

import akka.persistence.typed.scaladsl.{Effect, ReplyEffect}
import stars.webapi.impl.persistence.SimulationCommand.Create
import stars.webapi.impl.persistence.SimulationEvent.Created
import stars.webapi.impl.persistence.SimulationState.RE
import stars.webapi.protocol.SimulationOrder

object SimulationState {

  case object Empty extends SimulationState {

    override def apply(command: SimulationCommand): RE = command match {
      case Create(order, replyTo) =>
        Effect.persist(Created(order)).thenReply(replyTo) {
          case Waiting(storedOrder) => Right(storedOrder)
          case _ => Left(order -> new IllegalStateException())
        }
    }

    override def apply(event: SimulationEvent): SimulationState = event match {
      case Created(order) => Waiting(order)
      case _ => throw new UnsupportedOperationException
    }
  }

  type RE = ReplyEffect[SimulationEvent, SimulationState]

  case class Waiting(order: SimulationOrder) extends SimulationState {

    override def apply(command: SimulationCommand): RE = {
      throw new UnsupportedOperationException
    }

    override def apply(event: SimulationEvent): SimulationState = {
      throw new UnsupportedOperationException
    }
  }
}

sealed trait SimulationState {

  def apply(command: SimulationCommand): RE

  def apply(event: SimulationEvent): SimulationState
}