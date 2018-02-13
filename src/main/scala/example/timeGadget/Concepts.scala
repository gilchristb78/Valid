package example.timeGadget

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.{Omega, Type}
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
import time._


class Concepts(val gadget:Gadget) extends SemanticTypes {

  class ExtremeRange(ef:ExtremaFeature, wf:WeatherFeature) {
    def apply() : Seq[BodyDeclaration[_]] = {

      // weather feature updates a fixed period
      // extrema only requires to record at that internal frequency
      val ef_period = frequencyToSecond(ef.unit) * ef.count
      val wf_period = frequencyToSecond(wf.unit) * wf.count
      val size:Int = (ef_period / wf_period).asInstanceOf[Int]
        Java(s"""
           |public float getTemperatureRnd(java.time.LocalDateTime currentTime) {
           |  return (float) (40 + Math.random()*50);
           |}
           |
           |int[] values = null;
           |int idx = 0;
           |int[] _extremes = new int[2];
           |int[] getExtremes(float t) {
           |   if (values == null) {
           |     values = new int[$size];
           |     for (int i = 0; i < values.length; i++) { values[i] = (int)t; }
           |   } else {
           |     values[idx++] = (int) t;
           |     if (idx == $size) { idx = 0; }
           |   }
           |   _extremes[0] = _extremes[1] = (int) t;
           |   for (int old : values) {
           |     if (old < _extremes[0]) { _extremes[0] = old; }
           |     if (old > _extremes[1]) { _extremes[1] = old; }
           |   }
           |   return _extremes;
           |}""".stripMargin).classBodyDeclarations()
    }

    val semanticType:Type = artifact(artifact.extraCode, feature(FeatureUnit.Extrema))
  }

  class CurrentTemperature(wf: WeatherFeature) {

    def apply(conversion: Expression => Expression): Seq[BodyDeclaration[_]] = {
      val zipCode = wf.zip
      val period = frequencyToSecond(wf.unit) * wf.count

      Java(s"""|
            |private float lastTemp = 0;
            |private java.time.LocalDateTime lastChecked = null;
            |public float getTemperature(java.time.LocalDateTime currentTime) {
            |  if (lastChecked == null || currentTime.isAfter(lastChecked.plusSeconds($period))) {
            |    try {
            |	    java.net.URL url = new java.net.URL("http://api.weatherunlocked.com/api/forecast/us.$zipCode?app_id={APPID}&app_key={APPKEY}");
            |      java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader (url.openStream()));
            |      StringBuffer sb = new StringBuffer(br.readLine()); br.close();
            |      int c = sb.indexOf("temp_c");
            |      float celsiusTemperature = Float.valueOf(sb.substring(c+9, sb.indexOf(",", c)));
            |      lastTemp = ${conversion(Java("celsiusTemperature").expression[Expression]())};
            |    } catch (Exception e) { return Float.NaN; }
            |    lastChecked = currentTime;
            |  }
            |  return lastTemp;
            |}""".stripMargin).classBodyDeclarations()
    }

    val semanticType:Type =
      feature.temperature.converter(TemperatureUnit.Celsius, temperatureUnit) =>:
        artifact(artifact.extraCode, temperatureUnit :&: feature(FeatureUnit.Weather))
  }

  @combinator object FahrenheitConverter {
    def apply: Expression => Expression = { celsius => Java(s"((9/5.0f)*$celsius + 32)").expression[Expression]() }
    val semanticType: Type = feature.temperature.converter(TemperatureUnit.Celsius,
      temperatureUnit :&: feature.temperature(TemperatureUnit.Fahrenheit))
  }

  @combinator object CelsiusStraight {
    def apply: Expression => Expression = { celsius => Java(s"$celsius").expression[Expression]() }
    val semanticType: Type = feature.temperature.converter(TemperatureUnit.Celsius,
      temperatureUnit :&: feature.temperature(TemperatureUnit.Celsius))
  }
//
//  @combinator object EmptyFeatureDeclarations {
//    def apply: Seq[BodyDeclaration[_]] = Seq.empty
//    val semanticType: Type = artifact(artifact.extraCode, Omega)
//  }
//
//  @combinator object EmptyFeatureLoopCode {
//    def apply: Seq[BodyDeclaration[_]] = Seq.empty
//    val semanticType: Type = artifact(artifact.loopCode, Omega)
//  }

  class MainCode {
    def apply(extraDecl: Seq[BodyDeclaration[_]], extraCode: Expression => Seq[Statement]):
       CompilationUnit = {

      val freqFeature = gadget.getFeature(FeatureUnit.Frequency).get.asInstanceOf[FrequencyFeature]

      val totalSec:Long = freqFeature.count * frequencyToSecond(freqFeature.unit) * 1000
      Java(
        s"""public class GadgetCode {
           |  ${extraDecl.mkString("\n")}
           |  public void loop() {
           |    try { Thread.sleep(${totalSec}); } catch (Exception ex) {}
           |    java.time.LocalDateTime now = java.time.LocalDateTime.now();
           |
           |    ${extraCode(Java("now").expression()).mkString("\n")}
           |  }
           |  public static void main(String[] args) {
           |    GadgetCode gadget = new GadgetCode();
           |    while (true) { gadget.loop(); }
           |  }
           |}""".stripMargin).compilationUnit()
    }

    val semanticType: Type = artifact(artifact.combinedCode, featureType) =>:
        artifact(artifact.loopCode, featureType) =>:
        artifact(artifact.mainProgram, featureType :&: feature(FeatureUnit.Frequency))
  }


  // Show temperature only
  class ShowTemperature {
    def apply: Expression => Seq[Statement] = theCurrentTime =>
      Java(
        s"""
           |float temperature = getTemperature($theCurrentTime);
           |System.out.println($theCurrentTime + ": The temperature is: " + temperature);""".stripMargin).statements()
    val semanticType: Type = artifact(artifact.loopCode, feature(FeatureUnit.Weather))
  }

  // process temperature in given expression to compute extrema
  class ProcessExtrema {
    def apply: Expression => Seq[Statement] = temperature =>
            Java(s"""
                 |int[] extreme = getExtremes($temperature);
                 |System.out.printf("The Temperature is in range[%d,%d]%n", extreme[0], extreme[1]);
                 |
                 """.stripMargin).statements()

    val semanticType: Type = artifact(artifact.loopCode, feature(FeatureUnit.Extrema))
  }

  // Combined. This seems like this could be done a better way...
  class Combined_Temp_Extrema {
    def apply () : Expression => Seq[Statement] = theCurrentTime => {

        new ShowTemperature().apply(theCurrentTime) ++
        new ProcessExtrema().apply(Java("temperature").expression())
    }
    val semanticType: Type = artifact(artifact.loopCode, feature(FeatureUnit.Weather) :&: feature(FeatureUnit.Extrema))
  }

  // Combined. This seems like it could be done a better way..
  class Combined_Temp_Extrema_Code {
    def apply (temp:Seq[BodyDeclaration[_]]) : Seq[BodyDeclaration[_]] = {

      val wf = gadget.getFeature(FeatureUnit.Weather).get.asInstanceOf[WeatherFeature]
      val ef = gadget.getFeature(FeatureUnit.Extrema).get.asInstanceOf[ExtremaFeature]

      temp ++ new ExtremeRange(ef, wf).apply()
    }

    val semanticType: Type = artifact(artifact.extraCode, temperatureUnit :&: feature(FeatureUnit.Weather)) =>:
      artifact(artifact.combinedCode, feature(FeatureUnit.Weather) :&: feature(FeatureUnit.Extrema))
  }
}


