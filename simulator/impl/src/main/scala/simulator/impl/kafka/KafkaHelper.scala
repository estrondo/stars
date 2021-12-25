package simulator.impl.kafka

import akka.Done
import akka.actor.ClassicActorSystemProvider
import akka.kafka.scaladsl.Consumer.Control
import akka.kafka.scaladsl.{Consumer, DiscoverySupport, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}

import scala.concurrent.Future

object KafkaHelper {

  def createPlainSink[T](config: Config)(adapter: T => ProducerRecord[String, Array[Byte]])
    (implicit system: ClassicActorSystemProvider): Sink[T, Future[Done]] = {

    val settings = ProducerSettings(config, new StringSerializer, new ByteArraySerializer)
      .withEnrichAsync(DiscoverySupport.producerBootstrapServers(config))

    Flow
      .fromFunction(adapter)
      .toMat(Producer.plainSink(settings))(Keep.right)
  }

  def createPlainSource[T](
    topics: Set[String],
    groupId: String,
    config: Config
  )(adapter: ConsumerRecord[String, Array[Byte]] => T)(implicit system: ClassicActorSystemProvider): Source[T, Control] = {

    val settings = ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
      .withEnrichAsync(DiscoverySupport.consumerBootstrapServers(config))
      .withGroupId(groupId)

    Consumer
      .plainSource(settings, Subscriptions.topics(topics))
      .map(adapter)
  }
}
