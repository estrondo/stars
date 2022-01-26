package webapi

import akka.kafka.scaladsl.SendProducer
import com.softwaremill.macwire.wire
import org.scalatest.AsyncTestSuite
import webapi.simulator.gateway.SimulatorGatewayKafka

trait WithSimulatorGateway {
  this: AsyncTestSuite with WithActorSystem with WithKafkaSettings =>

  val SimulatorTopic = "simulator-topic"

  lazy val sendProducer: SendProducer[String, Array[Byte]] = SendProducer(producerSettings)

  lazy val simulatorGateway: SimulatorGatewayKafka = wire[SimulatorGatewayKafka]

}
