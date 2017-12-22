package example.timeGadget

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.{Omega, Type}
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
import time.TemperatureUnit

trait Concepts extends SemanticTypes with VariableDeclarations {

  class CurrentTemperature(zipCode: String) {
    def apply(conversion: Expression => Expression): Seq[BodyDeclaration[_]] =
      Java(s"""|private float lastTemperature = 0;
               |private java.time.LocalDateTime lastChecked = null;
               |
               |public float getTemperature(java.time.LocalDateTime currentTime) {
               |  if (lastChecked == null || currentTime.isAfter(lastChecked.plusMinutes(30))) {
               |    try {
               |	    java.net.URL url = new java.net.URL("http://api.weatherunlocked.com/api/forecast/us.$zipCode?app_id={APPID}&app_key={APPKEY}");
               |      java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader (url.openStream()));
               |      StringBuffer sb = new StringBuffer(br.readLine());
               |      int c = sb.indexOf("temp_c");
               |      float celsiusTemperature = Float.valueOf(sb.substring(c+9, sb.indexOf(",", c)));
               |      lastTemperature = ${conversion(Java("celsiusTemperature").expression[Expression]())};
               |    } catch (Exception e) { return Float.NaN; }
               |    lastChecked = currentTime;
               |  }
               |  return lastTemperature;
               |}""".stripMargin).classBodyDeclarations()

    val semanticType:Type =
      converter(feature.temperature(TemperatureUnit.Celsius), temperatureUnit) =>:
        artifact(artifact.extraCode, feature(temperatureUnit))
  }

  @combinator object FahrenheitConverter {
    def apply: Expression => Expression = { celsius => Java(s"((9/5.0f)*$celsius + 32)").expression[Expression]() }
    val semanticType: Type = converter(feature.temperature(TemperatureUnit.Celsius), feature.temperature(TemperatureUnit.Fahrenheit))
  }
  @combinator object KelvinConverter {
    def apply: Expression => Expression = { celsius => Java(s"($celsius + 273.15f)").expression[Expression]() }
    val semanticType: Type = converter(feature.temperature(TemperatureUnit.Celsius), feature.temperature(TemperatureUnit.Kelvin))
  }
  @combinator object CelsiusConverter {
    def apply: Expression => Expression = { celsius => celsius }
    val semanticType: Type = converter(feature.temperature(TemperatureUnit.Celsius), feature.temperature(TemperatureUnit.Celsius))
  }

  @combinator object ShowTemperature {
    def apply: Expression => Seq[Statement] = theCurrentTime =>
      Java(
        s"""
           |System.out.println("The temperature is: " + getTemperature($theCurrentTime));
         """.stripMargin).statements()
    val semanticType: Type = artifact(artifact.loopCode, feature(temperatureUnit))
  }

  @combinator object EmptyFeatureDeclarations {
    def apply: Seq[BodyDeclaration[_]] = Seq.empty
    val semanticType: Type = artifact(artifact.extraCode, Omega)
  }

  @combinator object EmptyFeatureLoopCode {
    def apply: Seq[BodyDeclaration[_]] = Seq.empty
    val semanticType: Type = artifact(artifact.loopCode, Omega)
  }

  @combinator object MainCode {
    def apply(
      extraDeclarations: Seq[BodyDeclaration[_]],
      extraLoopCode: Expression => Seq[Statement]): CompilationUnit =
      Java(
        s"""public class TimeGadget {
           |  ${extraDeclarations.mkString("\n")}
           |
           |  public void loop() {
           |    try { Thread.sleep(1000); } catch (Exception ex) {}
           |    java.time.LocalDateTime now = java.time.LocalDateTime.now();
           |
           |    ${extraLoopCode(Java("now").expression()).mkString("\n")}
           |    System.out.println(now);
           |  }
           |
           |  public static void main(String[] args) {
           |    TimeGadget timeGadget = new TimeGadget();
           |    while (true) {
           |      timeGadget.loop();
           |    }
           |  }
           |}
         """.stripMargin
      ).compilationUnit()

    val semanticType: Type =
      artifact(artifact.extraCode, featureType) =>:
        artifact(artifact.loopCode, featureType) =>:
        artifact(artifact.mainProgram, featureType :&: feature(feature.time))
  }
}


