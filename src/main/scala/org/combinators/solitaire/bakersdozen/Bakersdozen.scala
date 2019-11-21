package org.combinators.solitaire.bakersdozen
import javax.inject.Inject
import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, InhabitationController, Results, RoutingEntries}
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.domain.Solitaire
import org.combinators.solitaire.shared.cls.Synthesizer
import org.webjars.play.WebJarsUtil
import org.combinators.templating.persistable.JavaPersistable._
import play.api.inject.ApplicationLifecycle
import org.combinators.solitaire.spanish_patience.spanish_patience
import org.combinators.solitaire.castles_in_spain.castles_in_spain


abstract class BakersdozenVariationController(web: WebJarsUtil, app: ApplicationLifecycle) extends InhabitationController(web, app) with RoutingEntries {

  lazy val variation:Solitaire = bakersdozen

  lazy val repository = new gameDomain(variation) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), variation)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets = Synthesizer.allTargets(variation)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override val routingPrefix = Some("bakersdozen")
  lazy val controllerAddress: String = variation.name.toLowerCase

}

class BakersdozenController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends BakersdozenVariationController(webJars, applicationLifecycle) {
  val f0 = System.nanoTime()
  override lazy val variation = bakersdozen
  val f1 = System.nanoTime()
  println("---BASE FAN TIME: " + (f1-f0) + " ns---")
}

class Spanish_PatienceController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends BakersdozenVariationController(webJars, applicationLifecycle) {
  override lazy val variation = spanish_patience
}

class Castles_In_SpainController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends BakersdozenVariationController(webJars, applicationLifecycle) {
  override lazy val variation = castles_in_spain
}


