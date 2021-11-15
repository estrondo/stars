package stars.simulator

import akka.persistence.testkit.scaladsl.EventSourcedBehaviorTestKit
import com.typesafe.config.Config
import stars.textkit.addSystemProperty

trait AbstractSimulatorSpecWithoutKafka extends Spec {


  override protected def beforeAll(): Unit = {
    addSystemProperty(
      "KAFKA_CONSUMER_PORT" -> "port",
      "KAFKA_PRODUCER_PORT" -> "port",
      "KAFKA_CONSUMER_HOST" -> "localhost",
      "KAFKA_PRODUCER_HOST" -> "localhost"
    )
    super.beforeAll()
  }

  override protected def createConfig(): Config =
    EventSourcedBehaviorTestKit.config.withFallback(super.createConfig())
}
