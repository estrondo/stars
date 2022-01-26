package webapi.module

import akka.actor.typed.{ActorSystem, DispatcherSelector, Extension, ExtensionId}
import akka.kafka.scaladsl.{Consumer, DiscoverySupport, SendProducer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.util.Timeout
import akkahelper._
import com.softwaremill.macwire.wire
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}
import webapi.core.gateway.SimulatorGateway
import webapi.core.repository.SimulationRepository
import webapi.core.service.SimulatorService
import webapi.core.service.impl.SimulatorServiceImpl
import webapi.simulator.entity.SimulationEntityExt
import webapi.simulator.gateway.SimulatorGatewayKafka
import webapi.simulator.repository.SimulationRepositoryImpl

import scala.annotation.nowarn
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.FiniteDuration

object SimulatorModule extends ExtensionId[SimulatorModule] {

  override def createExtension(system: ActorSystem[_]): SimulatorModule = {
    new SimulatorModule()(system)
  }
}

class SimulatorModule(implicit system: ActorSystem[_]) extends Extension {

  private def consumerSettings = {
    val config = system.config.getConfig("stars.webapi.simulator.kafka.consumer")
    ConsumerSettings(
      config = config,
      keyDeserializer = new StringDeserializer(),
      valueDeserializer = new ByteArrayDeserializer()
    ).withEnrichAsync(DiscoverySupport.consumerBootstrapServers(config))
  }

  private def producerSettings = {
    val config = system.config.getConfig("stars.webapi.simulator.kafka.producer")
    ProducerSettings(
      config = config,
      keySerializer = new StringSerializer(),
      valueSerializer = new ByteArraySerializer()
    ).withEnrichAsync(DiscoverySupport.producerBootstrapServers(config))
  }

  @nowarn private def consumer = {
    val topic = system.config.getString("stars.webapi.simulator.kafka.consumer-topic")
    Consumer.plainSource(consumerSettings, Subscriptions.topics(topic))
  }

  val simulatorGateway: SimulatorGateway = {
    //noinspection ScalaUnusedSymbol
    @nowarn val topic = system.config.getString("stars.webapi.simulator.kafka.producer-topic")
    //noinspection ScalaUnusedSymbol
    @nowarn val sendProducer = SendProducer(producerSettings)
    //noinspection TypeAnnotation
    @nowarn implicit val executor = system.dispatchers.lookup(DispatcherSelector.fromConfig("dispatchers.kafka"))
    wire[SimulatorGatewayKafka]
  }

  val simulationRepository: SimulationRepository = {
    //noinspection TypeAnnotation
    @nowarn implicit val timeout = Timeout(system.config.as[FiniteDuration]("stars.webapi.simulator-repository.timeout"))
    //noinspection ScalaUnusedSymbol
    @nowarn val shardingRegion = SimulationEntityExt(system).sharding
    wire[SimulationRepositoryImpl]
  }

  @nowarn
  val simulatorService: SimulatorService = {
    implicit val executor: ExecutionContextExecutor =
      system.dispatchers.lookup(DispatcherSelector.fromConfig("stars.dispatchers.simulator"))

    wire[SimulatorServiceImpl]
  }

  //noinspection ScalaUnusedSymbol
  @nowarn
  private val simulatorConfiguration = system.config.as[SimulatorServiceImpl.Configuration]("stars.webapi.simulator-service")
}
