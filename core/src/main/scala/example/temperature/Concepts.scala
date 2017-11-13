package example.temperature

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.`type`.{Type => JType}
import com.github.javaparser.ast.expr.Expression
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import de.tu_dortmund.cs.ls14.cls.types.Type

trait Concepts extends SemanticTypes {
  @combinator object FloatType { val semanticType:Type = precision(precision.floating)
  def apply() : JType = Java("float").tpe() }
  @combinator object Truncate {
    def apply(exp:Expression) : Expression = Java(s"""((int)$exp)""").expression()
    val semanticType:Type = artifact(artifact.compute) :&: precision(precision.floating) =>:
                            artifact(artifact.compute) :&: precision(precision.integer)
  }
  @combinator object IntegerType { val semanticType:Type = precision(precision.integer)
    def apply() : JType = Java("int").tpe() }
  @combinator object CurrentWorcesterWeather {
    def apply(): CompilationUnit = {
      Java(s"""|import java.io.*;
               |import java.net.*;
               |public class WorcesterWeather {
               |  public float getTemperature() {
               |		try {
               |			URL url = new URL("http://api.weatherunlocked.com/api/forecast/us.01609?app_id={APPID}&app_key={APPKEY}");
               |			BufferedReader br = new BufferedReader(new InputStreamReader (url.openStream()));
               |			StringBuffer sb = new StringBuffer(br.readLine());
               |			int c = sb.indexOf("temp_c");
               |			return Float.valueOf(sb.substring(c+9, sb.indexOf(",", c)));
               |		} catch (Exception e) { return Float.NaN; }
               |	}
               |}""".stripMargin).compilationUnit()
    }
    val semanticType:Type = artifact(artifact.api) :&: precision(precision.floating) :&: scale(scale.celsius)
  }
  @combinator object UpInterface {
    def apply(temp:Expression, precision:JType): CompilationUnit = {
      Java(s"""|public class TemperatureAdapter {
               |  $precision getTemperature() {
               |    return $temp;
               |  }
               |}""".stripMargin).compilationUnit()
    }
    val semanticType:Type = artifact(artifact.compute) :&: precision(precisionType) =>:
                            precision(precisionType)  =>:
                            artifact(artifact.converter) :&: precision(precisionType)
  }
  @combinator object CelsiusToFahrenheit {
    def apply(cels:Expression):Expression = {
       Java("""((9.0/5.0)*""" + cels.toString + """ + 32.0)""").expression()
    }
    val semanticType:Type = scale(scale.celsius) =>:
                            artifact(artifact.compute) :&: precision(precision.floating) :&: scale(scale.fahrenheit)
  }
  @combinator object TemperatureAPI {
    def apply:Expression = {
      Java(s"""WorcesterWeather.getTemperature()""").expression()
    }
    val semanticType:Type = artifact(artifact.compute) :&: precision(precision.floating) :&: scale(scale.celsius)
  }
}


