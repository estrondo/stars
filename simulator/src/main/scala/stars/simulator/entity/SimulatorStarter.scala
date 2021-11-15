package stars.simulator.entity

import akka.actor.typed.scaladsl.ActorContext
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.stream.Materializer
import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import stars.simulation.protocol.{NewSimulation, SimulationCommand, SimulationCommandMessage, StartSimulation, Topics}
import stars.simulator._
import stars.simulator.kafka.{KafkaHelper, SourceConnector}

object SimulatorStarter extends StrictLogging {

  val CommandIdExtractor: SimulationCommand => Option[String] = {
    case NewSimulation(id, _, _) => Some(id)
    case StartSimulation(id, _) => Some(id)
    case ignoring =>
      logger.warn("Ignoring message: {}.", ignoring)
      None
  }

  val RecordToEntityCommand: ConsumerRecord[String, Array[Byte]] => SimulationCommand = { record =>
    SimulationCommandMessage.parseFrom(record.value()).toSimulationCommand
  }

  def apply(context: ActorContext[_]): Unit = try {
    logger.debug("Starting Simulator Sharding.")
    val sharding = ClusterSharding(context.system)
    sharding.init(Simulator.createEntity(context.getConfig("stars.simulator-entity")))

    val source = KafkaHelper
      .createPlainSource(
        topics = Set(Topics.SimulationCommandTopic),
        groupId = "simulator",
        config = context.getConfig("stars.kafka.consumer")
      )(RecordToEntityCommand)(context.system)

    SourceConnector
      .toEntity(source, Simulator.TypeKey, sharding)(CommandIdExtractor)(Materializer(context))
  } catch {
    case cause: Exception =>
      logger.error("Impossible to start Simulator Sharding!", cause)
  }
}
