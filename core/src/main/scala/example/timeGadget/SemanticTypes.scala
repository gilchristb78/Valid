package example.timeGadget

import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import time.TemperatureUnit


trait SemanticTypes {
  object converter {
    def apply(from: Type, to: Type) = 'Converter(from, to)
  }

  object feature {
    def apply(featureType: Type): Type = 'Feature(featureType)
    object temperature {
      def apply(in: TemperatureUnit): Type = 'TemperatureIn(Constructor(in.toString))
    }
    val time: Type = 'Time
  }

  object artifact {
    def apply(part:Type, forFeature: Type):Type = 'Artifact(part, forFeature)

    val extraCode: Type   = 'ImplementationOf
    val loopCode: Type    = 'LoopCodeFor
    val mainProgram: Type = 'ProgramWith
  }
}



//
//  // when you want subtyping use a taxonomy
//  // by making this a FIELD it will be detected and then accessible. Note
//  // these params are strings but they will be referenced as 'Kelvin, i.e.
//  val taxonomyScales = Taxonomy("Scale").
//    addSubtype(scale.celsius.toString).
//    addSubtype(scale.fahrenheit.toString).
//    addSubtype(scale.kelvin.toString)
