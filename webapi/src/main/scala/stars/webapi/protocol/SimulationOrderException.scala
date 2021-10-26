package stars.webapi.protocol

class SimulationOrderException(val order: SimulationOrder, cause: Throwable = null) extends RuntimeException(cause)
