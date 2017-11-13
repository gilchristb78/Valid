package example.temperature

import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._

/**
  * Generic small temperature example
  */
trait SemanticTypes {

  val alpha = Variable("TempType")

  // by making this a FIELD it will be detected and then accessible. Note
  // these params are strings but they will be referenced as 'Kelvin, i.e.
  val taxonomyScales = Taxonomy(scale.label.toString).
    addSubtype(scale.celsius.toString).
    addSubtype(scale.fahrenheit.toString).
    addSubtype(scale.kelvin.toString)

  object scale {
    val label:Type = 'Temperature

    val celsius:Type = 'Celsius
    val fahrenheit:Type = 'Fahrenheit
    val kelvin:Type = 'Kelvin
  }

  object precision {
    def apply (tpe:Type) = 'Precision(tpe)
    def unit:Type = 'Unit

    val integer: Type = 'Integer
    val floating:Type = 'Float
  }

  object artifact {
    def apply (part:Type) = 'Artifact(part)

    val expression:Type = 'Expression
    val interface:Type = 'Interface
  }
}
