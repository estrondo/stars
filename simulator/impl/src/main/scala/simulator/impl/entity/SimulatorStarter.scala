package simulator.impl.entity

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.kafka.scaladsl.Consumer
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import simulator._
import simulator.impl.{SimulatorException, StreamHelper}
import simulator.impl.context.ContextGenerator
import simulator.impl.entity.Simulator.Configuration
import simulator.impl.kafka.KafkaHelper
import simulator.protocol.{CreateSimulation, SimulationCommand, SimulationCommandMessage, Topics}

object SimulatorStarter extends StrictLogging {

  val CommandCollector: SimulationCommand => Option[(String, Message)] = {
    case command@CreateSimulation(Some(simulation), _) =>
      Some((simulation.id, Message.Command(command)))
    case ignoring =>
      logger.warn("Ignoring command: {}.", ignoring)
      None
  }

  val RecordToSimulationCommand: ConsumerRecord[String, Array[Byte]] => SimulationCommand = { record =>
    logger.debug("Parsing Record [{}].", record.key())
    SimulationCommandMessage.parseFrom(record.value()).toSimulationCommand
  }

  def apply(
    system: ActorSystem[_],
    contextGenerator: ActorRef[ContextGenerator.Message]
  )(implicit materializer: Materializer): ActorRef[ShardingEnvelope[Message]] = try {
    logger.debug("Creating Simulator Kafka Source.")
    val source = createKafkaSource(system.config("stars.kafka.consumer"))(system)

    logger.debug("Creating Simulator Shard Region.")
    val sharding = ClusterSharding(system)
    val region = sharding.init(Simulator.createEntity(new Configuration(
      validator = Validator(system.config("stars.simulator.entity.validator")),
      contextGenerator = contextGenerator
    )))

    logger.debug("Connecting Kafka SimulationCommand to Simulation Shard Region.")
    connect(source, region)

    region
  } catch {
    case cause: Exception =>
      throw SimulatorException.Unexpected("Impossible to start Simulator Shard Region!", cause)
  }

  def connect[M](
    source: Source[SimulationCommand, M],
    region: ActorRef[ShardingEnvelope[Message]]
  )(implicit materializer: Materializer): M = {
    StreamHelper(source).toShardRegion(region)(CommandCollector)
  }

  def createKafkaSource(config: Config, groupId: String = "stars-simulator")
    (implicit system: ActorSystem[_]): Source[SimulationCommand, Consumer.Control] = {

    KafkaHelper
      .createPlainSource(
        topics = Set(Topics.SimulationCommandTopic),
        groupId = groupId,
        config = config
      )(RecordToSimulationCommand)(system)
  }
}
