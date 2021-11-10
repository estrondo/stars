package stars.simulator

object SimulationException {

  case class IllegalState(message: String, cause: Throwable = null) extends SimulationException(message, cause)

  case class Invalid(message: String, cause: Throwable = null) extends SimulationException(message, cause)

  case class InvalidID(message: String, cause: Throwable = null) extends SimulationException(message, cause)

  case class InvalidMessage(message: String, cause: Throwable = null) extends SimulationException(message, cause)
}

abstract class SimulationException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)