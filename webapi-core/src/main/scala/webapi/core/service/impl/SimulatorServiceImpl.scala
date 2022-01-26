package webapi.core.service.impl

import com.typesafe.scalalogging.StrictLogging
import octopus.dsl._
import octopus.syntax._
import util.IDGenerator
import webapi.core.WebApiException.AlreadyExist
import webapi.core.gateway.SimulatorGateway
import webapi.core.repository.SimulationRepository
import webapi.core.service.SimulatorService
import webapi.core.service.impl.SimulatorServiceImpl.Configuration
import webapi.core.{BlackHoleDescription, SimulationDescription, SimulationReceipt, WebApiException}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object SimulatorServiceImpl {

  case class Configuration(
      maxStars: Int,
      defaultStars: Int,
      minStarMass: Double,
      maxStarMass: Double,
      minBlackHoleMass: Double,
      maxBlackHoleMass: Double
  )
}

class SimulatorServiceImpl(
    repository: SimulationRepository,
    gateway: SimulatorGateway,
    configuration: Configuration
)(implicit executor: ExecutionContext)
    extends SimulatorService
    with StrictLogging {

  private implicit val blackHoleValidator: Validator[BlackHoleDescription] = {
    import configuration._
    Validator[BlackHoleDescription]
      .rule[Double](
        _.solarMass,
        x => x >= minBlackHoleMass && x <= maxBlackHoleMass,
        s"Black Hole solarMass must be between $minBlackHoleMass and $maxBlackHoleMass!"
      )
  }

  private implicit val descriptionValidator: Validator[SimulationDescription] = {
    import configuration._
    Validator[SimulationDescription]
      .rule[Int](_.stars, x => x >= 0 && x <= maxStars, s"number of stars must be less than $maxStars!")
      .rule[Double](
        _.maxStarMass,
        x => x >= minStarMass && x <= maxStarMass,
        s"maxStarMass must be between $minStarMass and $maxStarMass!"
      )
      .rule[Double](
        _.minStarMass,
        x => x >= minStarMass && x <= maxStarMass,
        s"minStarMass must be between $minStarMass and $maxStarMass!"
      )
      .rule[Double](_.massDistribution, x => x >= -1 && x <= 1, "massDistribution must be between -1 and 1!")
      .compose(_.blackHoles.flatMap(_.validate.errors).toList)
  }

  override def register(
      description: SimulationDescription
  ): Future[SimulationReceipt] = {
    for {
      newDescription <- Future(validate(description))
      newID <- prepareNewSimulation()
      _ <- gateway.send(newID, newDescription)
    } yield {
      logger.info("Simulation {} was accepted.", newID)
      SimulationReceipt(newID, newDescription)
    }
  }

  private def prepareNewSimulation(): Future[String] = {
    tryNewSimulationID(IDGenerator.generate(16))
  }

  private def tryNewSimulationID(newID: String): Future[String] = {
    logger.debug("Trying new simulation ID {}.", newID)
    repository.allocate(newID).transformWith {
      case Success(_) =>
        Future.successful(newID)
      case Failure(_: AlreadyExist) =>
        logger.debug("There is already simulation ID {}.", newID)
        tryNewSimulationID(IDGenerator.generate(16))
      case Failure(cause) =>
        Future.failed(cause)
    }
  }

  // noinspection SameParameterValue
  private def updateUndefined[T](
      value: T,
      undefinedValue: T,
      defaultValue: T
  ): T = {
    if (value != undefinedValue) value else defaultValue
  }

  private def validate(
      description: SimulationDescription
  ): SimulationDescription = {
    description
      .copy(
        stars = updateUndefined(description.stars, 0, configuration.defaultStars),
        minStarMass = updateUndefined(description.minStarMass, 0, configuration.minStarMass),
        maxStarMass = updateUndefined(description.maxStarMass, 0, configuration.maxStarMass)
      )
      .validate
      .toEither match {
      case Right(newDescription) =>
        newDescription
      case Left(errors) =>
        val messages = for (error <- errors) yield {
          logger.warn("Simulation {} has an error: {}.", error.message)
          error.message
        }

        throw new WebApiException.MultiMessageException("Invalid Simulation!", messages)
    }
  }
}
