package stars.webapi.impl.persistence.simulation

import akka.persistence.typed.scaladsl.Effect
import stars.webapi.impl.persistence.SimulationCommand.Create
import stars.webapi.impl.persistence.{SimulationCommand, SimulationEvent, SimulationState}
import stars.webapi.impl.persistence.SimulationState.RE

trait WaitingBehavior extends SimulationStateBehavior {

  override def apply(command: SimulationCommand): RE = command match {
    case Create(order, replyTo) => Effect.reply(replyTo)(Left(order -> new IllegalStateException()))
    case _ => throw new UnsupportedOperationException
  }

  override def apply(event: SimulationEvent): SimulationState = {
    throw new UnsupportedOperationException
  }
}
