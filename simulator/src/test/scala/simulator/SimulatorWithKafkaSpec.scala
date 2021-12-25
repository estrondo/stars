package simulator

import akka.kafka.Subscriptions
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import simulation.protocol.Topics
import simulator.context.ContextGenerator
import simulator.entity.{Simulator, SimulatorStarter, Validator}
import testkit.fixture.simulation.SimulationFixture
import textkit.newRandomId

class SimulatorWithKafkaSpec extends SimulatorWithKafkaSpecLike {

  "An Entity should to inform context generator" in {

    val id = newRandomId()
    val simulation = SimulationFixture.newNewSimulation(id)

    val contextGenerator = testKit.createTestProbe[ContextGenerator.Message]()

    val configuration = new Simulator.Configuration(
      validator = Validator(0.1, 20, 100, 5000),
      contextGenerator = contextGenerator.ref
    )

    val region = sharding.init(Simulator.createEntity(configuration))
    val source = Consumer
      .plainSource(consumerSetting, Subscriptions.topics(Topics.SimulationCommandTopic))
      .map(SimulatorStarter.RecordToSimulationCommand)

    SimulatorStarter.connect(source, region)

    Producer
      .plainSink(producerSetting)
      .runWith(Source.single(new ProducerRecord(Topics.SimulationCommandTopic, id, simulation.toByteArray)))

    contextGenerator.expectMessageType[ContextGenerator.Generate]

  }
}
