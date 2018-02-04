package example.timeGadget

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git.{EmptyResults, InhabitationController, RoutingEntries}
import org.combinators.templating.persistable.JavaPersistable._
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle
import time.{TemperatureUnit, TimeGadget}

class TimeGadgetController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries   {

  // domain model
  val gadget = new TimeGadget("Worcester", "01609", TemperatureUnit.Fahrenheit)

  lazy val repository = new Concepts {}
  import repository._
  lazy val Gamma = ReflectedRepository(repository, substitutionSpace=kinding, classLoader = this.getClass.getClassLoader)
        .addCombinator(new CurrentTemperature(gadget.zip))
  lazy val combinatorComponents = Gamma.combinatorComponents

  // request artifacts for the solution domain. Call for a main program with temperature features from application domain
  lazy val jobs = Gamma.InhabitationBatchJob[CompilationUnit](artifact(artifact.mainProgram, feature(feature.temperature(gadget.temperatureUnit))))

  lazy val results = EmptyResults().addAll(jobs.run())

  lazy val controllerAddress: String = "timegadget"
}
