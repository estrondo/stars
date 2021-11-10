package stars.webapi.impl.infra

import akka.actor.typed.ActorRef
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.{DiscoverySupport, Producer}
import akka.stream.OverflowStrategy
import akka.stream.typed.scaladsl.ActorSource
import com.lightbend.lagom.scaladsl.server.LagomServerComponents
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import scalapb.GeneratedMessage
import stars.simulation.protocol.{NewSimulation, SimulationCommand, StartSimulation}


trait SimulationDispatcherComponentRequired {

  def simulationDispatcher: ActorRef[SimulationCommand]
}

trait KafkaSimulationDispatcherComponent extends SimulationDispatcherComponentRequired {
  this: LagomServerComponents =>

  lazy val toProducerRecord: SimulationCommand => ProducerRecord[String, Array[Byte]] = { command =>
    val id = command match { // Ok, it's ugly.
      case NewSimulation(id, _, _) => id
      case StartSimulation(id, _) => id
    }

    val bytes = command.asInstanceOf[GeneratedMessage].toByteArray
    new ProducerRecord(id + "-" + System.currentTimeMillis(), bytes)
  }

  lazy val simulationDispatcher: ActorRef[SimulationCommand] = {
    val (actor, source) = ActorSource
      .actorRef[SimulationCommand](
        completionMatcher = PartialFunction.empty,
        failureMatcher = PartialFunction.empty,
        bufferSize = 25,
        overflowStrategy = OverflowStrategy.fail
      )
      .preMaterialize()(materializer)

    val config = actorSystem.settings.config.getConfig("lagom.broker.kafka.client.producer")
    implicit val _actorSystem = actorSystem

    val settings = ProducerSettings(
      config = config,
      keySerializer = new StringSerializer(),
      valueSerializer = new ByteArraySerializer()
    ).withEnrichAsync(DiscoverySupport.producerBootstrapServers(config))

    val sink = Producer.plainSink(settings)
    source.map(toProducerRecord).runWith(sink)(materializer)
    actor
  }
}
