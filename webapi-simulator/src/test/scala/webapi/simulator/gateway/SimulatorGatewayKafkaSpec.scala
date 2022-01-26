package webapi.simulator.gateway

import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import simulator.protocol.Simulation
import util.IDGenerator
import webapi.core.fixture.SimulationDescriptionFixture

class SimulatorGatewayKafkaSpec extends AbstractSimulatorGatewayKafkaSpec {

  "A SimulatorGateway" - {
    "when receive a valid simulation" - {
      "should to send it to kafka" in {
        val newID = IDGenerator.generate()
        val gateway = new SimulatorGatewayKafka("simulation-test", sendProducer)(executionContext)
        val description = SimulationDescriptionFixture.newDescription()
        val source = Consumer.plainSource(
          consumerDefaults(new StringDeserializer, new ByteArrayDeserializer)
            .withGroupId("testers"),
          Subscriptions.topics("simulation-test")
        )

        for {
          receipt <- gateway.send(newID, description)
          Some(record) <- source.runWith(Sink.headOption)
        } yield {
          receipt.id should be(newID)
          val restored = Simulation.parseFrom(record.value())
          restored.id should be (newID)
          restored.email should be(description.email)
          restored.name should be(description.name)
          restored.stars should be(description.stars)
        }
      }
    }
  }
}
