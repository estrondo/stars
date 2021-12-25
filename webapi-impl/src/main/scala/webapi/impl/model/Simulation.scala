package webapi.impl.model

import java.util.UUID

case class Simulation(id: UUID, owner: String, email: String) extends Serializable