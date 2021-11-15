package stars.simulator.entity

import com.typesafe.config.Config
import stars.simulation.protocol.{Description, SolarMass}
import stars.simulator.entity.Validator.{AttValidator, Error}

object Validator {

  class AttValidator[T](
    reference: T,
    predicate: (T, T) => Boolean,
    title: String,
    message: (T, T) => String
  ) {

    def apply(value: T, errors: Seq[Error]): Seq[Error] = {
      if (predicate(reference, value))
        errors
      else
        errors :+ new Error(title, message(reference, value))
    }

    def apply[C](value: T, context: C, errors: Seq[Error], message: (T, T, C) => String): Seq[Error] = {
      if (predicate(reference, value))
        errors
      else
        errors :+ new Error(title, message(reference, value, context))
    }
  }

  class Error(val title: String, val description: String) {

    override def toString: String = s"Error(title=$title, description=$description)"
  }

  def apply(config: Config): Validator = apply(
    minStarMass = config.getDouble("min-star-mass"),
    maxStarMass = config.getDouble("max-star-mass"),
    maxStars = config.getInt("max-stars"),
    maxBlackHoleMass = config.getDouble("max-black-hole-mass")
  )

  def apply(
    minStarMass: SolarMass,
    maxStarMass: SolarMass,
    maxStars: Int,
    maxBlackHoleMass: SolarMass
  ): Validator = new Validator(
    minStarMass = new AttValidator(minStarMass, _ <= _, "minStarMass", (r, v) => s"$v < $r."),
    maxStarMass = new AttValidator(maxStarMass, _ >= _, "maxStarMass", (r, v) => s"$v > $r."),
    maxStars = new AttValidator(maxStars, _ >= _, "maxStars", (r, v) => s"$v > $r."),
    maxBlackHoleMass = new AttValidator(maxBlackHoleMass, _ <= _, "blackHoleMass", (r, v) => s"$v > $r.")
  )
}

class Validator(
  minStarMass: AttValidator[SolarMass],
  maxStarMass: AttValidator[SolarMass],
  maxStars: AttValidator[Int],
  maxBlackHoleMass: AttValidator[SolarMass]
) {

  def apply(description: Description): Either[Seq[Error], Description] = {
    var errors = minStarMass(description.minStarMass, Seq.empty)
    errors = maxStarMass(description.maxStarMass, errors)
    errors = maxStars(description.stars, errors)
    for ((blackHole, idx) <- description.blackHoles.zipWithIndex) {
      errors = maxBlackHoleMass[Int](blackHole.solarMass, idx, errors, (r, v, i) => s"Black Hole #$i: $v > $r.")
    }

    if (errors.isEmpty) Right(description) else Left(errors)
  }
}