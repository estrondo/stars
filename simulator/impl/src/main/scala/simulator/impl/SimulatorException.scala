package simulator.impl

object SimulatorException {

  case class IllegalState(message: String, cause: Throwable = null) extends SimulatorException(message, cause)

  case class Invalid(message: String, cause: Throwable = null) extends SimulatorException(message, cause)

  case class InvalidID(message: String, cause: Throwable = null) extends SimulatorException(message, cause)

  case class InvalidMessage(message: String, cause: Throwable = null) extends SimulatorException(message, cause)

  case class Unexpected(message: String, cause: Throwable = null) extends SimulatorException(message, cause)
}

abstract class SimulatorException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)