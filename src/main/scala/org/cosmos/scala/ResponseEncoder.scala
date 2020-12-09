package org.cosmos.scala

import org.cosmos.scala.JsonEncoderInstances.given
import org.cosmos.scala.{EnvironmentResponse, JsonEncoder, MoonResponse, PlanetResponse}


given JsonEncoder[BodyName] = JsonEncoder.toStringEncoder(_.asString)

given JsonEncoder[Radius] = JsonEncoder.toDoubleEncoder(_.asDouble)

// JsonInstances are derived separately to the corresponding class definition (I just prefer this way),
// but it can be combined with class definition with derives keyword.
given JsonEncoder[EnvironmentResponse] = JsonEncoder.derived

given JsonEncoder[MoonResponse] = JsonEncoder.derived

given JsonEncoder[PlanetResponse] = JsonEncoder.derived
