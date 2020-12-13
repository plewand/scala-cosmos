package org.cosmos.scala

import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers._
import scala.collection.immutable._
import ServiceError._


// An example test to show Id monad usage in tests with Scala 3 givens.
// The tests may be synchrounous and their logic is simpler therefore.
class StartSystemServiceTest extends AnyFlatSpec with should.Matchers {

  import LittleMonadInstances.{given LittleMonad[Id]}

  // To have extension methods
  import BodyName._
  import Radius._

  "StarSystemService" should "get full planet information" in {
    val startSystemService = StarSystemService[Id]

    val result = startSystemService.getFullData("sun".asBodyName, "earth".asBodyName, None)

    val moonResponse = MoonResponse("Moon".asBodyName, 1737.1.asRadius,
      EnvironmentResponse(HabitabilityConditions.Hostile, SurfaceMaterial.Rock, AtmosphereGas.Missing))

    val expected = PlanetResponse("Earth".asBodyName, 6371.0.asRadius,
      EnvironmentResponse(HabitabilityConditions.Confirmed, SurfaceMaterial.Rock, AtmosphereGas.Nitrogen),
      Seq(moonResponse))

    result shouldBe expected
  }

  it should "get full moon information" in {
    val startSystemService = StarSystemService[Id]

    val result = startSystemService.getFullData("sun".asBodyName, "earth".asBodyName, Some("moon".asBodyName))

    val expected = MoonResponse("Moon".asBodyName, 1737.1.asRadius,
      EnvironmentResponse(HabitabilityConditions.Hostile, SurfaceMaterial.Rock, AtmosphereGas.Missing))

    result shouldBe expected
  }

  it should "get planet environment" in {
    val startSystemService = StarSystemService[Id]

    val result = startSystemService.getEnvironment("sun".asBodyName, "earth".asBodyName, None)

    val expected = EnvironmentResponse(HabitabilityConditions.Confirmed, SurfaceMaterial.Rock, AtmosphereGas.Nitrogen)

    result shouldBe expected
  }

  it should "get moon environment" in {
    val startSystemService = StarSystemService[Id]

    val result = startSystemService.getEnvironment("sun".asBodyName, "earth".asBodyName, Some("moon".asBodyName))

    val expected = EnvironmentResponse(HabitabilityConditions.Hostile, SurfaceMaterial.Rock, AtmosphereGas.Missing)

    result shouldBe expected
  }

  it should "not allow to pass empty system name on getting full information" in {
    val startSystemService = StarSystemService[Id]

    val result = startSystemService.getFullData("".asBodyName, "earth".asBodyName, None)

    result shouldBe ValidationError("Name validation error: system valid: false, planet valid: true, moon valid: true")
  }

  it should "not allow to pass empty planet name on getting full information" in {
    val startSystemService = StarSystemService[Id]

    val result = startSystemService.getFullData("sun".asBodyName, "".asBodyName, None)

    result shouldBe ValidationError("Name validation error: system valid: true, planet valid: false, moon valid: true")
  }

  it should "not allow to pass empty moon name on getting full information" in {
    val startSystemService = StarSystemService[Id]

    val result = startSystemService.getFullData("sun".asBodyName, "earth".asBodyName, Some("".asBodyName))

    result shouldBe ValidationError("Name validation error: system valid: true, planet valid: true, moon valid: false")
  }

  it should "not allow to pass empty system name on getting environment" in {
    val startSystemService = StarSystemService[Id]

    val result = startSystemService.getEnvironment("".asBodyName, "earth".asBodyName, None)

    result shouldBe ValidationError("Name validation error: system valid: false, planet valid: true, moon valid: true")
  }

  it should "not allow to pass empty planet name on getting environment" in {
    val startSystemService = StarSystemService[Id]

    val result = startSystemService.getEnvironment("sun".asBodyName, "".asBodyName, None)

    result shouldBe ValidationError("Name validation error: system valid: true, planet valid: false, moon valid: true")
  }

  it should "not allow to pass empty moon name on getting environment" in {
    val startSystemService = StarSystemService[Id]

    val result = startSystemService.getEnvironment("sun".asBodyName, "earth".asBodyName, Some("".asBodyName))

    result shouldBe ValidationError("Name validation error: system valid: true, planet valid: true, moon valid: false")
  }
}
