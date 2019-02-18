package org.combinators.solitaire.golf

import com.github.javaparser.ast.CompilationUnit
import javax.inject.Inject
import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, InhabitationController, Results, RoutingEntries}
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.domain.Solitaire
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.templating.persistable.JavaPersistable._
import org.combinators.solitaire.golf.golf
import org.combinators.solitaire.golf_no_wrap.golf_no_wrap
import org.combinators.solitaire.allInARow.allInARow
import org.combinators.solitaire.flake.flake
import org.combinators.solitaire.flake_two_decks.flake_two_decks
import org.combinators.solitaire.robert.robert
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle

class GolfVariationController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries {

  lazy val variation:Solitaire = golf

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new golfDomain(variation) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), variation)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(variation)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override val routingPrefix = Some("golf")

  lazy val controllerAddress: String = variation.name.toLowerCase
}

  class GolfController @Inject() (webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
    extends GolfVariationController(webJars, applicationLifecycle) {
    val g0 = System.nanoTime()
    override lazy val variation = golf
    val g1 = System.nanoTime()
    println("---BASE GOLF TIME: " + (g1-g0) + " ns---")

  }

  class Golf_no_wrapController @Inject() (webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
    extends GolfVariationController(webJars, applicationLifecycle) {
    override lazy val variation = golf_no_wrap
  }

  class AllInARowController @Inject() (webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
    extends GolfVariationController(webJars, applicationLifecycle) {
    override lazy val variation = allInARow
  }

  class FlakeController @Inject() (webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
    extends GolfVariationController(webJars, applicationLifecycle) {
    val f0 = System.nanoTime()
    override lazy val variation = flake
    val f1 = System.nanoTime()
    println("---FLAKE GOLF TIME: " + (f1-f0) + " ns---")

  }

class Flake_two_decksController @Inject() (webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends GolfVariationController(webJars, applicationLifecycle) {
  override lazy val variation = flake_two_decks
}

class RobertController @Inject() (webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends GolfVariationController(webJars, applicationLifecycle) {
  val r0 = System.nanoTime()
  override lazy val variation = robert
  val r1 = System.nanoTime()
  println("---ROBERT GOLF TIME: " + (r1-r0) + " ns---")

}


