package org.cosmos.scala

import org.cosmos.scala.Conversions.bodyNameConversion
import org.cosmos.scala.StarSystemDsl.{moon, noMoonPlanet, planet, starSystem}

import scala.language.implicitConversions

// This simple project contains predefined data and no database access.
// Note usage of the DSL.
class StarSystemRepository[F[_] : LittleMonad] {

  import BodyName._
  import LittleMonadExtensions.pure

  private val solarSystem = starSystem("Sun".asBodyName) {
    noMoonPlanet("Mercury".asBodyName, Radius(2439.7), HabitabilityConditions.Hostile, SurfaceMaterial.Rock, AtmosphereGas.Missing)
    noMoonPlanet("Venus".asBodyName, Radius(6051.8), HabitabilityConditions.Hostile, SurfaceMaterial.Rock, AtmosphereGas.CarbonDioxide)
    planet("Earth".asBodyName, Radius(6371.0), HabitabilityConditions.Confirmed, SurfaceMaterial.Rock, AtmosphereGas.Nitrogen) {
      moon("Moon".asBodyName, Radius(1737.1), HabitabilityConditions.Hostile, SurfaceMaterial.Rock, AtmosphereGas.Missing)
    }
    planet("Mars".asBodyName, Radius(3389.5), HabitabilityConditions.Possible, SurfaceMaterial.Sand, AtmosphereGas.CarbonDioxide) {
      moon("Phobos".asBodyName, Radius(12.0), HabitabilityConditions.Hostile, SurfaceMaterial.Rock, AtmosphereGas.Missing)
      moon("Deimos".asBodyName, Radius(6.0), HabitabilityConditions.Hostile, SurfaceMaterial.Rock, AtmosphereGas.Missing)
    }
    planet("Jupiter".asBodyName, Radius(69911.0), HabitabilityConditions.Hostile, SurfaceMaterial.Liquid, AtmosphereGas.Hydrogen) {
      moon("Io".asBodyName, Radius(1843.2), HabitabilityConditions.Hostile, SurfaceMaterial.Rock, AtmosphereGas.Missing)
      moon("Europa".asBodyName, Radius(1521.6), HabitabilityConditions.Possible, SurfaceMaterial.Ice, AtmosphereGas.Missing)
      moon("Ganymede".asBodyName, Radius(2562.4), HabitabilityConditions.Hostile, SurfaceMaterial.Rock, AtmosphereGas.Missing)
      moon("Calisto".asBodyName, Radius(2410.6), HabitabilityConditions.Hostile, SurfaceMaterial.Rock, AtmosphereGas.Missing)
      // Let me skip ~80 moons left.
    }
    planet("Saturn".asBodyName, Radius(58232.0), HabitabilityConditions.Hostile, SurfaceMaterial.Liquid, AtmosphereGas.Hydrogen) {
      moon("Enceladus".asBodyName, Radius(256.2), HabitabilityConditions.Hostile, SurfaceMaterial.Ice, AtmosphereGas.Missing)
      moon("Europa".asBodyName, Radius(3121.6), HabitabilityConditions.Possible, SurfaceMaterial.Sand, AtmosphereGas.Methane)
    }
    planet("Uranus".asBodyName, Radius(25362.0), HabitabilityConditions.Hostile, SurfaceMaterial.Liquid, AtmosphereGas.Hydrogen) {
      moon("Miranda".asBodyName, Radius(236.2), HabitabilityConditions.Hostile, SurfaceMaterial.Ice, AtmosphereGas.Missing)
      moon("Titania".asBodyName, Radius(763.6), HabitabilityConditions.Possible, SurfaceMaterial.Rock, AtmosphereGas.Missing)
    }
    planet("Neptune".asBodyName, Radius(24622.0), HabitabilityConditions.Hostile, SurfaceMaterial.Liquid, AtmosphereGas.Hydrogen) {
      moon("Triton".asBodyName, Radius(847.5), HabitabilityConditions.Hostile, SurfaceMaterial.Ice, AtmosphereGas.Missing)
    }
  }

  private val alphaCentauri = starSystem("AlphaCentauri".asBodyName) {
    noMoonPlanet("ProximaB".asBodyName, Radius(5000.0), HabitabilityConditions.Possible, SurfaceMaterial.Ice, AtmosphereGas.Nitrogen)
    noMoonPlanet("ProximaC".asBodyName, Radius(10000.0), HabitabilityConditions.Hostile, SurfaceMaterial.Liquid, AtmosphereGas.Methane)
  }

  private val starSystems = Seq(solarSystem, alphaCentauri)

  // An example of union type in the return value. All 3 classes instances can appear there.
  def getBody(systemName: BodyName, planetName: BodyName, moonNameOpt: Option[BodyName]): F[Moon | Planet | Undefined] = {
    val planetOpt = for {
      // Implicit conversion is safely used here to access String methods.
      system <- starSystems.find(_.name.equalsIgnoreCase(systemName))
      planet <- system.planets.find(_.name.equalsIgnoreCase(planetName))
    } yield planet

    val result: Moon | Planet | Undefined = (planetOpt, moonNameOpt) match {
      case (Some(planet), Some(moonName)) => planet.moons.find(_.name.equalsIgnoreCase(moonName)).getOrElse(Undefined)
      case (Some(planet), None) => planet
      case other => Undefined
    }
    result.pure
  }
}
