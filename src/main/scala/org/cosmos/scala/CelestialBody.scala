package org.cosmos.scala


trait Habitability(val conditions: HabitabilityConditions)

trait Surface(val material: SurfaceMaterial)

trait Atmosphere(val gas: AtmosphereGas)

trait Moons(val moons: Seq[Moon])


// A common base class for planets and moons.
abstract class CelestialBody(val name: BodyName, val radius: Radius)

// CelestialBody with some parametrized traits. The traits represents some features of the body.
class Planet(bodyName: BodyName,
             bodyRadius: Radius,
             habitability: HabitabilityConditions,
             surface: SurfaceMaterial,
             atmosphere: AtmosphereGas,
             planetMoons: Seq[Moon])
  extends CelestialBody(bodyName, bodyRadius) with Habitability(habitability) with Surface(surface) with Atmosphere(atmosphere) with Moons(planetMoons)


class Moon(bodyName: BodyName,
           bodyRadius: Radius,
           habitability: HabitabilityConditions,
           surface: SurfaceMaterial,
           atmosphere: AtmosphereGas)
  extends CelestialBody(bodyName, bodyRadius) with Habitability(habitability) with Surface(surface) with Atmosphere(atmosphere)


// Planets are gathered into a star system.
class StarSystem(val name: BodyName, val planets: Seq[Planet])
