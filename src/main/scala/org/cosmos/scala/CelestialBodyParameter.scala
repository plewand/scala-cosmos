package org.cosmos.scala


enum HabitabilityConditions {

  case Confirmed

  case Possible

  case Hostile

}

// Enums can have parameters. In this case they are constant as defined in each enum value.
enum SurfaceMaterial(val albedo: Double) {

  case Rock extends SurfaceMaterial(0.4)

  case Sand extends SurfaceMaterial(0.6)

  case Ice extends SurfaceMaterial(0.95)

  case Liquid extends SurfaceMaterial(0.5)

}

enum AtmosphereGas {

  case CarbonDioxide

  case Hydrogen

  case Nitrogen

  case Methane

  case Missing

}

// Opaque types to represent domain values.
opaque type Radius = Double

// Some utilities to work with the opaque type. It is possible to create the type instance both with apply
// and extension method.
object Radius {
  def apply(number: Double): Radius = number

  extension (radius: Radius)
    def asDouble: Double = radius

  extension (number: Double)
    def asRadius: Radius = number
}

opaque type BodyName = String

object BodyName {
  def apply(str: String): BodyName = str

  extension (bodyName: BodyName)
    def asString: String = bodyName

  extension (str: String)
    def asBodyName: BodyName = str
}

// It is safe to use implicit conversions in the domain, so this mechanism is used.
// In Scala 3 all implicit conversions must be derived from Conversion class.
object Conversions {

  given radiusConversion : Conversion[Radius, Double] with
    def apply(radius: Radius): Double = radius.asDouble


  given bodyNameConversion: Conversion[BodyName, String] with
    def apply(bodyName: BodyName): String = bodyName.asString

}

