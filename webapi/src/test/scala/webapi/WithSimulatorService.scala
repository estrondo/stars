package webapi

import com.softwaremill.macwire.wire
import org.scalatest.AsyncTestSuite
import webapi.core.service.impl.SimulatorServiceImpl

trait WithSimulatorService extends WithSimulationRepository with WithSimulatorGateway {
  this: AsyncTestSuite with WithActorSystem with WithKafkaSettings =>

  lazy val simulatorService: SimulatorServiceImpl = wire[SimulatorServiceImpl]

  protected def SimulatorServiceConfiguration: SimulatorServiceImpl.Configuration = SimulatorServiceImpl.Configuration(
    maxStars = 10000,
    defaultStars = 500,
    minStarMass = 0.1,
    maxStarMass = 10,
    minBlackHoleMass = 10,
    maxBlackHoleMass = 500
  )
}
