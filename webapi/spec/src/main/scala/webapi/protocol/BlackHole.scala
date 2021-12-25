package webapi.protocol

import simulator.protocol.{Pc, SolarMass}

case class BlackHole(solarMass: SolarMass, x: Pc, y: Pc, z: Pc)
