package org.combinators.solitaire.fan
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
import org.combinators.solitaire.fanfreepile.fanfreepile
import org.combinators.solitaire.shamrocks.shamrocks
import org.combinators.solitaire.scotchpatience.scotchpatience
import org.combinators.solitaire.fantwodeck.fantwodeck
import org.combinators.solitaire.faneasy.faneasy
import org.combinators.solitaire.labellelucie.labellelucie
import org.combinators.solitaire.superflowergarden.superflowergarden
import org.combinators.solitaire.alexanderthegreat.alexanderthegreat
import org.combinators.solitaire.shared.SemanticTypes
import org.combinators.solitaire.trefoil.trefoil


abstract class FanVariationController(web: WebJarsUtil, app: ApplicationLifecycle) extends InhabitationController(web, app) with RoutingEntries {

  lazy val variation:Solitaire =  fan

  lazy val repository = new FanDomain(variation) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), variation)

  lazy val combinatorComponents = Gamma.combinatorComponents

  // TEMPORARILY BREAK for all other Fan variations.
  lazy val targets = Synthesizer.allTargets(variation) ++ Synthesizer.newTargets()

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override val routingPrefix = Some("fan")
  lazy val controllerAddress: String = variation.name.toLowerCase

}

class FanController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FanVariationController(webJars, applicationLifecycle) {
  val f0 = System.nanoTime()
  override lazy val variation = fan
  val f1 = System.nanoTime()
  println("---BASE FAN TIME: " + (f1-f0) + " ns---")
}

class FanFreePileController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FanVariationController(webJars, applicationLifecycle) {
  override lazy val variation = fanfreepile
}

class ScotchPatienceController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FanVariationController(webJars, applicationLifecycle) {
  override lazy val variation = scotchpatience
}

class ShamrocksController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FanVariationController(webJars, applicationLifecycle) {
  val s0 = System.nanoTime()
  override lazy val variation = shamrocks
  val s1 = System.nanoTime()
  println("---SHAMROCKS FAN TIME: " + (s1-s0) + " ns---")
}

class FanTwoDeckController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FanVariationController(webJars, applicationLifecycle) {
  override lazy val variation = fantwodeck
}

class FanEasyController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FanVariationController(webJars, applicationLifecycle) {
  override lazy val variation = faneasy
}

class LaBelleLucieController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FanVariationController(webJars, applicationLifecycle) {
  override lazy val variation = labellelucie
}

class SuperFlowerGardenController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FanVariationController(webJars, applicationLifecycle) {
  override lazy val variation = superflowergarden
}

class TrefoilController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FanVariationController(webJars, applicationLifecycle) {
  override lazy val variation = trefoil
}

class AlexanderTheGreatController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FanVariationController(webJars, applicationLifecycle) {
  val a0 = System.nanoTime()
  override lazy val variation = alexanderthegreat
  val a1 = System.nanoTime()
  println("---ALEXANDER FAN TIME: " + (a1-a0) + " ns---")
}