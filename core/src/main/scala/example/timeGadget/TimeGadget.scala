package example.timeGadget

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.`type`.{Type => JType}
import com.github.javaparser.ast.expr.Expression
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import de.tu_dortmund.cs.ls14.java.JavaPersistable._
import org.webjars.play.WebJarsUtil
import time.{TemperatureUnit, TimeGadget}

class TimeGadgetController @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  val gadget = new TimeGadget("Worcester", TemperatureUnit.Kelvin)

  lazy val repository = new Concepts {}
  import repository._
  lazy val Gamma =
    ReflectedRepository(repository, kinding=kinding, classLoader = this.getClass.getClassLoader)
        .addCombinator(new CurrentTemperature(gadget.location))
  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val jobs = Gamma.InhabitationBatchJob[CompilationUnit](artifact(artifact.mainProgram, feature(feature.temperature(gadget.temperatureUnit))))

  lazy val results = Results.addAll(jobs.run())
}

// sample code showing how to directly invoke, without web service.
/*object Manual {

  def main(args: Array[String]): Unit = {
    lazy val repository = new Concepts {}
    import repository._
    lazy val Gamma = ReflectedRepository(repository, kinding = kinding)

    println("Expressions that return Fahrenheit")
    Gamma.inhabit[Expression](artifact(artifact.compute) :&: precision(precision.floating) :&: unit(unit.fahrenheit))
      .interpretedTerms.values.flatMap(_._2)
      .foreach(exp => println(exp))

  }
}*/