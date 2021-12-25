package webapi

import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.deser.{MessageSerializer, StrictMessageSerializer}
import com.lightbend.lagom.scaladsl.api.transport.MessageProtocol
import simulation.protocol.SimulationCommand

class CommandMessageSerializer extends StrictMessageSerializer[SimulationCommand] {

  override def deserializer(protocol: MessageProtocol):
  MessageSerializer.NegotiatedDeserializer[SimulationCommand, ByteString] = ???

  override def serializerForRequest:
  MessageSerializer.NegotiatedSerializer[SimulationCommand, ByteString] = ???

  override def serializerForResponse(acceptedMessageProtocols: Seq[MessageProtocol]):
  MessageSerializer.NegotiatedSerializer[SimulationCommand, ByteString] = ???
}
