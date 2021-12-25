package webapi.impl.database

import webapi.impl.model.Simulation

import java.util.UUID
import API._

class SimulationTable(tag: Tag) extends Table[Simulation](tag, Some("stars"), "simulation") {

  def * = (id, owner, email) <> ((Simulation.apply _).tupled, Simulation.unapply)

  def email = column[String]("email")

  def id = column[UUID]("id")

  def owner = column[String]("owner")
}