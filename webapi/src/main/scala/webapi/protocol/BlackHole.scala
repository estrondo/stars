package webapi.protocol

import simulation.protocol.{Pc, SolarMass}

case class BlackHole(solarMass: SolarMass, x: Pc, y: Pc, z: Pc)
