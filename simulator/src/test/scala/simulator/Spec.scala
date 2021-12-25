package simulator

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorSystem
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Milliseconds, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, OptionValues}

trait Spec
  extends AnyFreeSpecLike
    with Matchers
    with BeforeAndAfterAll
    with BeforeAndAfterEach
    with ScalaFutures
    with OptionValues {

  implicit val defaultPatience =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(50, Milliseconds))

  protected lazy val testKit: ActorTestKit = ActorTestKit(createConfig())

  protected implicit lazy val materializer: Materializer = Materializer(testKit.internalSystem)

  override protected def afterAll(): Unit = {
    super.afterAll()
    testKit.shutdownTestKit()
  }

  override protected def beforeEach(): Unit = {
    super.beforeEach()
  }

  protected def config: Config = testKit.config

  protected def createConfig(): Config = ConfigFactory.load()

  protected def sharding: ClusterSharding = ClusterSharding(testKit.system)

  protected implicit def system: ActorSystem[_] = testKit.internalSystem
}
