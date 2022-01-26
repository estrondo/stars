package webapi

import akka.kafka.ProducerSettings

trait WithKafkaSettings {

  def producerSettings: ProducerSettings[String, Array[Byte]]
}
