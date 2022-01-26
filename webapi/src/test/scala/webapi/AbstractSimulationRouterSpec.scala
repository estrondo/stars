package webapi
import akka.actor.typed.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.testkit.scaladsl.{KafkaSpec, TestcontainersKafkaLike}
import akkahelper.ShardActorRef
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import org.scalatest.BeforeAndAfterAll
import webapi.simulator.entity.{SimulationEntity, SimulationEntityExt}

import scala.concurrent.ExecutionContext

abstract class AbstractSimulationRouterSpec
    extends KafkaSpec(-1, 0, akka.actor.ActorSystem("test", ConfigTest.application))
    with HttpSpecLike
    with WithSimulatorServiceAdapter
    with TestcontainersKafkaLike
    with WithKafkaSettings
    with BeforeAndAfterAll {

  override val ec: ExecutionContext = system.dispatcher

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    setUp()
  }

  override def producerSettings: ProducerSettings[String, Array[Byte]] = producerDefaults(
    keySerializer = new StringSerializer,
    valueSerializer = new ByteArraySerializer
  )

  override protected def simulationEntityShardRef: ShardActorRef[SimulationEntity.Command] =
    SimulationEntityExt(ActorSystem.wrap(system)).sharding
}
