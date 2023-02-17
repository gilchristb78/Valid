package org.combinators.solitaire.fan

import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, Results}
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.templating.persistable.JavaPersistable._
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}

/***
abstract class FanVariationController(web: WebJarsUtil, app: ApplicationLifecycle) extends InhabitationController(web, app) with RoutingEntries {
***/
trait FanVariationT extends SolitaireSolution {

  lazy val repository = new FanDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  // if a variation required extra code, just add on, like...  ++ Synthesizer.newTargets()
  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override lazy val routingPrefix: Option[String] = Some("fan")
}

object FanMain extends DefaultMain with FanVariationT {
  lazy val solitaire = fan
}

// NOTE: While this is solvable, the generic SOLVE logic doesn't take into account
// moves to/from the free cells; this needs to be fixed.
object FanFreeMain extends DefaultMain with FanVariationT {
  lazy val solitaire = fanfreepile.fanfreepile
}

object ScotchPatienceMain extends DefaultMain with FanVariationT {
  lazy val solitaire = scotchpatience.scotchpatience
}

object ShamrocksMain extends DefaultMain with FanVariationT {
  lazy val solitaire = shamrocks.shamrocks
}

object FanTwoDeckMain extends DefaultMain with FanVariationT {
  lazy val solitaire = fantwodeck.fantwodeck
}

//class FanEasyController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
//  extends InhabitationController(webJars, applicationLifecycle) with FanVariationT {
//  override lazy val solitaire = faneasy
//}
object FanEasyDeckMain extends DefaultMain with FanVariationT {
  lazy val solitaire = faneasy.faneasy
}

//class LaBelleLucieController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
//  extends InhabitationController(webJars, applicationLifecycle) with FanVariationT {
//  override lazy val solitaire = labellelucie
//}
object LaBelleLucieMain extends DefaultMain with FanVariationT {
  lazy val solitaire = labellelucie.labellelucie
}

//class SuperFlowerGardenController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
//  extends InhabitationController(webJars, applicationLifecycle) with FanVariationT {
//  override lazy val solitaire = superflowergarden
//}
object SuperFlowerGardenMain extends DefaultMain with FanVariationT {
  lazy val solitaire = superflowergarden.superflowergarden
}

//class TrefoilController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
//  extends InhabitationController(webJars, applicationLifecycle) with FanVariationT {
//  override lazy val solitaire = trefoil
//}
object TrefoilMain extends DefaultMain with FanVariationT {
  lazy val solitaire = trefoil.trefoil
}

object AlexanderTheGreatMain extends DefaultMain with FanVariationT {
  lazy val solitaire = alexanderthegreat.alexanderthegreat
}
