package example.temperature

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Expression, SimpleName}
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.generic
import de.tu_dortmund.cs.ls14.cls.types.{Omega, Type}

trait Concepts extends generic.JavaIdioms with SemanticTypes {

  @combinator object FloatType {
    def apply() : SimpleName = Java("float").simpleName()
    val semanticType:Type = precision.floating
  }

  @combinator object IntegerType {
    def apply() : SimpleName = Java("int").simpleName()
    val semanticType:Type = precision.integer
  }

  @combinator object UpInterface {
    def apply(temp:Expression, precision:SimpleName): CompilationUnit = {
      Java(s"""|public class TemperatureAdapter {
               |  $precision getTemperature() {
               |    return $temp;
               |  }
               |}""".stripMargin).compilationUnit()
    }

    val semanticType:Type = artifact(artifact.expression) :&: Omega =>:
                            precision(precision.unit) :&: Omega =>:
                            artifact(artifact.interface) :&: Omega
  }

  @combinator object CelsiusToFahrenheit {
    def apply(cels:Expression):Expression = {
       Java("""((9.0/5.0)*""" + cels.toString + """ + 32.0)""").expression()
    }

    val semanticType:Type = precision.floating :&: scale.celsius =>:
                            artifact(artifact.expression) :&: precision.floating :&: scale.fahrenheit
  }

  @combinator object TemperatureAPI {
    def apply:Expression = {
      Java(s"""Temperature.getCurrentTemperature()""").expression()
    }

    val semanticType:Type = artifact(artifact.expression) :&: precision.floating :&: scale.celsius
  }

}


