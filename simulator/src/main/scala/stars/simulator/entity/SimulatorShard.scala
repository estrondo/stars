package stars.simulator.entity

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.kafka.scaladsl.Consumer
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import stars.simulation.protocol._
import stars.simulator._
import stars.simulator.kafka.KafkaHelper

object SimulatorShard extends StrictLogging {

  val CommandIdExtractor: SimulationCommand => Option[String] = {
    case NewSimulation(id, _, _) => Some(id)
    case StartSimulation(id, _) => Some(id)
    case ignoring =>
      logger.warn("Ignoring command: {}.", ignoring)
      None
  }

  val RecordToEntityCommand: ConsumerRecord[String, Array[Byte]] => SimulationCommand = { record =>
    logger.debug("Parsing Record [{}].", record.key())
    SimulationCommandMessage.parseFrom(record.value()).toSimulationCommand
  }

  def apply(system: ActorSystem[_])(implicit materializer: Materializer): ActorRef[ShardingEnvelope[SimulationCommand]] = try {
    logger.debug("Creating Simulator Kafka Source.")
    val source = createKafkaSource(system.config("stars.kafka.consumer"))(system)

    logger.debug("Creating Simulator Shard Region.")
    val sharding = ClusterSharding(system)
    val region = sharding.init(Simulator.createEntity(system.config("stars.simulator.entity")))

    logger.debug("Connecting Kafka SimulationCommand to Simulation Shard Region.")
    connect(source, region)

    region
  } catch {
    case cause: Exception =>
      throw SimulationException.Unexpected("Impossible to start Simulator Shard Region!", cause)
  }

  def connect[M](
    source: Source[SimulationCommand, M],
    region: ActorRef[ShardingEnvelope[SimulationCommand]]
  )(implicit materializer: Materializer): M = {
    StreamHelper.toShardRegion(source, region)(CommandIdExtractor)
  }

  def createKafkaSource(config: Config)
    (implicit system: ActorSystem[_]): Source[SimulationCommand, Consumer.Control] = {

    KafkaHelper
      .createPlainSource(
        topics = Set(Topics.SimulationCommandTopic),
        groupId = "simulator",
        config = config
      )(RecordToEntityCommand)(system)
  }
}
