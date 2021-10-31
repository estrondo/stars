package stars.webapi.impl

import stars.webapi.impl.model.Simulation

import java.util.UUID

package object database {

  import API._

  class SimulationTable(tag: Tag) extends Table[Simulation](tag, Some("stars"), "simulation") {

    def * = (id, owner, email) <> ((Simulation.apply _).tupled, Simulation.unapply)

    def email = column[String]("email")

    def id = column[UUID]("id")

    def owner = column[String]("owner")
  }

  val SimulationTable = TableQuery[SimulationTable]

}
