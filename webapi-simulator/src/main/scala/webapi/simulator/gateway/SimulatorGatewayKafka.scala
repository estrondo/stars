package webapi.simulator.gateway

import akka.kafka.scaladsl.SendProducer
import akka.stream.scaladsl.Source
import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import webapi.core.{SimulationDescription, SimulationReceipt, WebApiException}
import webapi.core.gateway.SimulatorGateway
import webapi.simulator.mapper.SimulationMapper
import util._

import scala.concurrent.{ExecutionContext, Future}

class SimulatorGatewayKafka(
    topic: String,
    producer: SendProducer[String, Array[Byte]]
)(implicit executor: ExecutionContext)
    extends SimulatorGateway
    with StrictLogging {

  override def send(id: String, description: SimulationDescription): Future[SimulationReceipt] = {
    (for {
      record <- Future(new ProducerRecord(topic, id, convert(id, description)))
      _ <- producer.send(record)
    } yield {
      logger.debug("Simulation {} has sent to Kafka.", id)
      SimulationReceipt(id, description)
    }) throwBy {
      new WebApiException.Unexpected(s"Impossible to send simulation $id!", _)
    }
  }

  private def convert(id: String, description: SimulationDescription): Array[Byte] = {
    SimulationMapper
      .fromSimulationDescription(id, description)
      .toByteArray
  }
}
