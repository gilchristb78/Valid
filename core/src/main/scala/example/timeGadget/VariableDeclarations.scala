package example.timeGadget

import de.tu_dortmund.cs.ls14.cls.types.{Kinding, Omega, Variable}
import time.TemperatureUnit

trait VariableDeclarations { self: SemanticTypes =>
  val temperatureUnit = Variable("TemperatureUnit")
  val featureType = Variable("FeatureType")

  val temperatureUnits: Kinding =
    TemperatureUnit.values().foldLeft(Kinding(temperatureUnit)){
      case (k, unit) => k.addOption(feature.temperature(unit))
    }

  val featureTypes: Kinding =
    TemperatureUnit.values().foldLeft(Kinding(featureType)){
      case (k, unit) => k.addOption(feature(feature.temperature(unit)))
    }.addOption(Omega)

  val kinding = temperatureUnits.merge(featureTypes)
}
