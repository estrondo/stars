package stars.webapi.protocol

import stars.simulation.protocol.{Pc, SolarMass}

case class BlackHole(solarMass: SolarMass, x: Pc, y: Pc, z: Pc)
