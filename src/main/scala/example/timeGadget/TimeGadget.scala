package example.timeGadget

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git.{EmptyResults, InhabitationController}
import org.combinators.templating.persistable.JavaPersistable._
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle
import time.{TemperatureUnit, TimeGadget}

class TimeGadgetController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) {

  val gadget = new TimeGadget("Worcester", "01609", TemperatureUnit.Fahrenheit)

  lazy val repository = new Concepts {}
  import repository._
  lazy val Gamma =
    ReflectedRepository(repository, substitutionSpace=kinding, classLoader = this.getClass.getClassLoader)
        .addCombinator(new CurrentTemperature(gadget.zip))
  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val jobs = Gamma.InhabitationBatchJob[CompilationUnit](artifact(artifact.mainProgram, feature(feature.temperature(gadget.temperatureUnit))))

  lazy val results = EmptyResults().addAll(jobs.run())
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