package webapi

import com.softwaremill.macwire.wire
import org.scalatest.AsyncTestSuite
import webapi.adapter.SimulatorServiceAdapter

trait WithSimulatorServiceAdapter extends WithSimulatorService with WithExecutionContext {
  this: AsyncTestSuite with WithActorSystem with WithKafkaSettings =>

  lazy val simulatorServiceAdapter: SimulatorServiceAdapter = wire[SimulatorServiceAdapter]
}
