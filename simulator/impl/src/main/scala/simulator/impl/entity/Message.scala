package simulator.impl.entity

import akka.serialization.Serializer
import simulator.protocol.{SimulationCommand, SimulationCommandMessage}

sealed trait Message

object Message {

  case class Command(command: SimulationCommand) extends Message


  class CommandSerializer extends Serializer {

    override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
      Command(SimulationCommandMessage.parseFrom(bytes).toSimulationCommand)
    }

    override def identifier: Int = 19820624

    override def includeManifest: Boolean = false

    override def toBinary(value: AnyRef): Array[Byte] = value match {
      case Command(command) => command.asMessage.toByteArray
    }
  }
}
