package example.temperature

import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._

trait SemanticTypes {

  // whenever you want a Generic (like in java) you use variable
  val unitType = Variable("UnitType")
  val precisionType = Variable("PrecisionType")

  val precisions:Kinding = Kinding(precisionType)
    .addOption(precision.floating)
    .addOption(precision.integer)

  val units:Kinding = Kinding(unitType)
    .addOption(unit.celsius)
    .addOption(unit.fahrenheit)
    .addOption(unit.kelvin)

  val taxonomyLoss = Taxonomy(precision.lossyPrecision.toString).
    addSubtype(precision.fullPrecision.toString)

  object unit {
    def apply (tpe:Type):Type = 'Unit(tpe)

    val celsius:Type    = 'Celsius
    val fahrenheit:Type = 'Fahrenheit
    val kelvin:Type     = 'Kelvin
  }

  object precision {
    def apply (tpe:Type):Type = 'Precision(tpe)

    val fullPrecision:Type = 'Full
    val lossyPrecision:Type = 'Lossy
    val integer: Type = 'Integer
    val floating:Type = 'Float
  }

  object artifact {
    def apply (part:Type):Type = 'Artifact(part)

    val api:Type     = 'WeatherAPI
    val compute:Type = 'Compute
    val impl:Type    = 'Impl
  }

  val kinding:Kinding = precisions.merge(units)
  val taxonomy:Taxonomy = taxonomyLoss
}
