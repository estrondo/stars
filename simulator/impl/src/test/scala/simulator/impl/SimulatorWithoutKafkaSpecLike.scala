package simulator.impl

import akka.persistence.testkit.scaladsl.EventSourcedBehaviorTestKit
import com.typesafe.config.Config
import testkit.addSystemProperty

trait SimulatorWithoutKafkaSpecLike extends Spec {


  override protected def beforeAll(): Unit = {
    addSystemProperty(
      "KAFKA_CONSUMER_PORT" -> "port",
      "KAFKA_PRODUCER_PORT" -> "port",
      "KAFKA_CONSUMER_HOST" -> "localhost",
      "KAFKA_PRODUCER_HOST" -> "localhost",
      "STAR_SIMULATOR_ENTITY_VALIDATOR_MIN_STAR_MASS" -> "0.1",
      "STAR_SIMULATOR_ENTITY_VALIDATOR_MAX_STAR_MASS" -> "10",
      "STAR_SIMULATOR_ENTITY_VALIDATOR_MAX_STARS" -> "1000",
      "STAR_SIMULATOR_ENTITY_VALIDATOR_MAX_BLACK_HOLE_MASS" -> "50"
    )
    super.beforeAll()
  }

  override protected def createConfig(): Config =
    EventSourcedBehaviorTestKit.config.withFallback(super.createConfig())
}
