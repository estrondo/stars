package stars.simulator

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorSystem
import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, OptionValues}
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Milliseconds, Seconds, Span}

trait Spec
  extends AnyFreeSpecLike
    with Matchers
    with BeforeAndAfterAll
    with BeforeAndAfterEach
    with ScalaFutures
    with OptionValues {

  implicit protected val patience: PatienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(50, Milliseconds))

  lazy val testKit: ActorTestKit = ActorTestKit(createConfig())

  implicit lazy val materializer: Materializer = Materializer(testKit.internalSystem)

  override protected def afterAll(): Unit = {
    super.afterAll()
    testKit.shutdownTestKit()
  }

  protected def config: Config = testKit.config

  protected def createConfig(): Config = ConfigFactory.load()

  protected def system: ActorSystem[_] = testKit.internalSystem
}
