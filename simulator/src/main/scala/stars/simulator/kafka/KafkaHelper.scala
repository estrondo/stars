package stars.simulator.kafka

import akka.Done
import akka.actor.ClassicActorSystemProvider
import akka.kafka.scaladsl.Consumer.Control
import akka.kafka.scaladsl.{Consumer, DiscoverySupport, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

import scala.concurrent.Future

object KafkaHelper {

  def createPlainSink[T](config: Config)(adapter: T => ProducerRecord[String, String])
    (implicit system: ClassicActorSystemProvider): Sink[T, Future[Done]] = {

    val settings = ProducerSettings(config, new StringSerializer, new StringSerializer)
      .withEnrichAsync(DiscoverySupport.producerBootstrapServers(config))

    Flow
      .fromFunction(adapter)
      .toMat(Producer.plainSink(settings))(Keep.right)
  }

  def createPlainSource(
    topics: Set[String],
    groupId: String,
    config: Config
  )(implicit system: ClassicActorSystemProvider): Source[ConsumerRecord[String, String], Control] = {

    val settings = ConsumerSettings(config, new StringDeserializer, new StringDeserializer)
      .withEnrichAsync(DiscoverySupport.consumerBootstrapServers(config))
      .withGroupId(groupId)

    Consumer
      .plainSource(settings, Subscriptions.topics(topics))
  }
}
