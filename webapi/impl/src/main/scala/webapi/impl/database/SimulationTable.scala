package webapi.impl.database

import webapi.impl.database.API._
import webapi.impl.model.Simulation

class SimulationTable(tag: Tag) extends Table[Simulation](tag, Some("stars"), "simulation") {

  def * = (id, owner, email) <> ((Simulation.apply _).tupled, Simulation.unapply)

  def email = column[String]("email")

  def id = column[String]("id")

  def owner = column[String]("owner")
}