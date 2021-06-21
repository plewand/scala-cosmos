package org.cosmos.scala

import scala.collection.mutable.ArrayBuffer

// An utility class to build a star system definition. For each immutable domain object it uses a corresponding builder class.
// It uses Scala 3 context functions to make DSL compact and clear.
object StarSystemDsl {

  case class MoonBuilder(name: BodyName, radius: Radius, habitability: HabitabilityConditions, surface: SurfaceMaterial, atmosphere: AtmosphereGas)

  case class PlanetBuilder(name: BodyName, radius: Radius, habitability: HabitabilityConditions, surface: SurfaceMaterial, atmosphere: AtmosphereGas,
                           moons: ArrayBuffer[MoonBuilder] = ArrayBuffer.empty)

  case class NoMoonPlanetBuilder(name: BodyName, radius: Radius, habitability: HabitabilityConditions, surface: SurfaceMaterial, atmosphere: AtmosphereGas)

  case class StarSystemBuilder(name: BodyName, planets: ArrayBuffer[PlanetBuilder] = ArrayBuffer.empty) {
    def build: StarSystem = {
      StarSystem(name, planets.map(convertPlanet).toSeq)
    }

    def convertPlanet(planetBuilder: PlanetBuilder): Planet = {
      // Creator application - no new keyword required even for classes.
      Planet(planetBuilder.name, planetBuilder.radius, planetBuilder.habitability, planetBuilder.surface, planetBuilder.atmosphere,
        planetBuilder.moons.map(convertMoon).toSeq)
    }

    def convertMoon(builder: MoonBuilder): Moon = {
      Moon(builder.name, builder.radius, builder.habitability, builder.surface, builder.atmosphere)
    }
  }


  def starSystem(starName: BodyName)(definePlanets: StarSystemBuilder ?=> Unit): StarSystem = {
    // Create the alias. When it is accessed the first time, StarSystemBuilder object is created in a thread safe way.
    given starSystem: StarSystemBuilder = StarSystemBuilder(starName)
    definePlanets
    starSystem.build
  }

  def planet(planetName: BodyName, radius: Radius, habitability: HabitabilityConditions, surface: SurfaceMaterial, atmosphere: AtmosphereGas)(defineMoons: PlanetBuilder ?=> Unit)(using StarSystemBuilder) = {
    given planet: PlanetBuilder = PlanetBuilder(planetName, radius, habitability, surface, atmosphere)
    summon[StarSystemBuilder].planets += planet
    defineMoons
  }

  def noMoonPlanet(planetName: BodyName, radius: Radius, habitability: HabitabilityConditions, surface: SurfaceMaterial, atmosphere: AtmosphereGas)(using StarSystemBuilder) = {
    val planet: PlanetBuilder = PlanetBuilder(planetName, radius, habitability, surface, atmosphere)
    summon[StarSystemBuilder].planets += planet
  }

  def moon(moonName: BodyName, radius: Radius, habitability: HabitabilityConditions, surface: SurfaceMaterial, atmosphere: AtmosphereGas)(using PlanetBuilder) = {
    given moon: MoonBuilder = MoonBuilder(moonName, radius, habitability, surface, atmosphere)
    summon[PlanetBuilder].moons += moon
  }

}
