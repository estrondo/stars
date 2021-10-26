package stars.webapi.impl.persistence

import akka.persistence.typed.scaladsl.{Effect, ReplyEffect}
import stars.webapi.impl.persistence.SimulationCommand.Create
import stars.webapi.impl.persistence.SimulationEvent.Created
import stars.webapi.impl.persistence.SimulationState.Reply
import stars.webapi.protocol.SimulationOrderID

object SimulationState {

  case object Empty extends SimulationState {

    override def apply(command: SimulationCommand): Reply = command match {
      case Create(order, replyTo) =>
        Effect.persist(SimulationEvent.Created(order)).thenReply(replyTo) {
          case Waiting(storedOrder) => Right(storedOrder)
          case _ => Left(order -> new IllegalStateException())
        }
    }

    override def apply(event: SimulationEvent): SimulationState = event match {
      case Created(order) => Waiting(order)
      case _ => throw new UnsupportedOperationException
    }
  }

  type Reply = ReplyEffect[SimulationEvent, SimulationState]

  case class Waiting(order: SimulationOrderID) extends SimulationState {

    override def apply(command: SimulationCommand): Reply = {
      throw new UnsupportedOperationException
    }

    override def apply(event: SimulationEvent): SimulationState = {
      throw new UnsupportedOperationException
    }
  }
}

sealed trait SimulationState {

  def apply(command: SimulationCommand): Reply

  def apply(event: SimulationEvent): SimulationState
}