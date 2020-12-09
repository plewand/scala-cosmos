package org.cosmos.scala

case class EnvironmentResponse(habitability: HabitabilityConditions, surface: SurfaceMaterial, atmosphere: AtmosphereGas)

object EnvironmentResponse {
  def apply(env: Habitability & Atmosphere & Surface): EnvironmentResponse = {
    EnvironmentResponse(env.conditions, env.material, env.gas)
  }
}

case class MoonResponse(name: BodyName, radius: Radius, environment: EnvironmentResponse)

object MoonResponse {
  def apply(moon: Moon): MoonResponse = {
    MoonResponse(moon.name, moon.radius, EnvironmentResponse(moon))
  }
}

case class PlanetResponse(name: BodyName, radius: Radius, environment: EnvironmentResponse, moons: Seq[MoonResponse])

object PlanetResponse {
  def apply(planet: Planet): PlanetResponse = {
    PlanetResponse(planet.name, planet.radius, EnvironmentResponse(planet), planet.moons.map(MoonResponse.apply))
  }
}


