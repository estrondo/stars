package simulator.entity

sealed trait State

object State {

  case class Empty(id: String) extends State
}
