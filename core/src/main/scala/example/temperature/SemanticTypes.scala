package example.temperature

import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._

/**
  * Generic small temperature example
  */
trait SemanticTypes {

  // whenever you want a Generic like in java you use variable
  val unitType = Variable("UnitType")
  val precisionType = Variable("PrecisionType")

  val precisions = Kinding(precisionType)
  .addOption(precision.floating)
    .addOption(precision.integer)

  val units = Kinding(unitType)
    .addOption(scale.celsius)
    .addOption(scale.fahrenheit)
    .addOption(scale.kelvin)

  val kinding = precisions.merge(units)
//
//  // when you want subtyping use a taxonomy
//  // by making this a FIELD it will be detected and then accessible. Note
//  // these params are strings but they will be referenced as 'Kelvin, i.e.
//  val taxonomyScales = Taxonomy("Scale").
//    addSubtype(scale.celsius.toString).
//    addSubtype(scale.fahrenheit.toString).
//    addSubtype(scale.kelvin.toString)

  object scale {
    def apply (tpe:Type) = 'Precision(tpe)

    val celsius:Type = 'Celsius
    val fahrenheit:Type = 'Fahrenheit
    val kelvin:Type = 'Kelvin
  }

  object precision {
    def apply (tpe:Type) = 'Precision(tpe)

    val integer: Type = 'Integer
    val floating:Type = 'Float
  }

  object artifact {
    def apply (part:Type) = 'Artifact(part)

    val api:Type = 'WeatherAPI
    val compute:Type = 'Compute
    val converter:Type = 'Converter
  }
}
