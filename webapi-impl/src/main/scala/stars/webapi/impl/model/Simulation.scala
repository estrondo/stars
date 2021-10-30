package stars.webapi.impl.model

import stars.webapi.protocol.SimulationOrder

import java.util.UUID

object Simulation {

  def from(order: SimulationOrder): Simulation = {
    import order.simulation._
    Simulation(id = order.id, owner = name, email = email)
  }
}

case class Simulation(id: UUID, owner: String, email: String) extends Serializable