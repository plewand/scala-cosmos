package org.cosmos.scala

sealed trait Undefined

case object Undefined extends Undefined

// An example of a simple ADT constructed with Scala 3 enums.
enum ServiceError {

  case NotFoundError(msg: String)

  case ValidationError(msg: String)

}

// Givens can be passed not only with using keyword, but also with context bounds.
class StarSystemService[F[_] : LittleMonad] {

  import ServiceError._
  import LittleMonadExtensions._

  val startSystemRepository = StarSystemRepository[F]()

  def getFullData(systemName: BodyName, planetName: BodyName, moonNameOpt: Option[BodyName]): F[PlanetResponse | MoonResponse | NotFoundError | ValidationError] = {
    getBody(systemName, planetName, moonNameOpt).map {
      case moon: Moon => MoonResponse(moon)
      case planet: Planet => PlanetResponse(planet)
      case error: (NotFoundError | ValidationError) => error
    }
  }

  def getEnvironment(systemName: BodyName, planetName: BodyName, moonNameOpt: Option[BodyName]): F[EnvironmentResponse | NotFoundError | ValidationError] = {
    getBody(systemName, planetName, moonNameOpt).map {
      // An intersection type usage. Only values having all 3 traits are required to contruct an environment response.
      case body: (Habitability & Atmosphere & Surface) => EnvironmentResponse(body)
      case error: (NotFoundError | ValidationError) => error
    }
  }

  // In the real life there would be rather StarSystemName, PlanetName, MoonName not to exchange it by accident.
  private def getBody(systemName: BodyName, planetName: BodyName, moonNameOpt: Option[BodyName]): F[Planet | Moon | ServiceError] = {
    (validateName(systemName), validateName(planetName), moonNameOpt.map(validateName).getOrElse(true)) match {
      case (true, true, true) => startSystemRepository.getBody(systemName, planetName, moonNameOpt).map {
        case body: (Planet | Moon) => body
        case Undefined => NotFoundError(s"$systemName/$planetName/${moonNameOpt.getOrElse("")} not found")
      }
      case (systemResult, planetResult, moonResult) =>
        ValidationError(s"Name validation error: system valid: $systemResult, planet valid: $planetResult, moon valid: $moonResult").pure
    }
  }

  def validateName(name: BodyName) = !name.asString.isBlank
}
