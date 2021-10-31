package stars.webapi

import com.lightbend.lagom.scaladsl.api.transport.{ExceptionMessage, TransportException}

import com.lightbend.lagom.scaladsl.api.transport.TransportErrorCode.{NotFound => TNotFound, InternalServerError => TInternal}

trait SimulationException

object SimulationException {

  case class NotFound(message: String = null, cause: Throwable = null)
    extends TransportException(TNotFound, exceptionMessage(this, message), cause) with SimulationException

  case class Unexpected(message: String = null, cause: Throwable = null)
    extends TransportException(TInternal, exceptionMessage(this, message), cause) with SimulationException

  def exceptionMessage(exception: SimulationException.type, detail: String): ExceptionMessage = {
    new ExceptionMessage(exception.getClass.getSimpleName, detail)
  }
}