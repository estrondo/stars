package stars.webapi.impl.infra

import akka.actor.typed.ActorRef
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.{DiscoverySupport, Producer}
import akka.stream.OverflowStrategy
import akka.stream.typed.scaladsl.ActorSource
import com.lightbend.lagom.scaladsl.server.LagomServerComponents
import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import scalapb.GeneratedMessage
import stars.simulation.protocol.{NewSimulation, SimulationCommand, StartSimulation}
import stars.webapi.impl.infra.KafkaSimulationHelper.newSimulationCommandConverter

object KafkaSimulationHelper extends StrictLogging {

  def newSimulationCommandConverter(): SimulationCommand => ProducerRecord[String, Array[Byte]] = { command =>
    val id = command match { // Ok, it's ugly.
      case NewSimulation(id, _, _) =>
        logger.debug("Converting NewSimulation({}).", id)
        id
      case StartSimulation(id, _) =>
        logger.debug("Converting StartSimulation({}).", id)
        id
    }

    val bytes = command.asInstanceOf[GeneratedMessage].toByteArray
    new ProducerRecord(id + "-" + System.currentTimeMillis(), bytes)
  }
}


trait KafkaSimulationDispatcherComponent extends SimulationDispatcherComponentRequired {
  this: LagomServerComponents =>

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
    source.map(newSimulationCommandConverter()).runWith(sink)(materializer)
    actor
  }
}