package org.combinators.solitaire.fan
import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, Results}
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.templating.persistable.JavaPersistable._
import org.combinators.solitaire.fanfreepile.fanfreepile
import org.combinators.solitaire.scotchpatience.scotchpatience
import org.combinators.solitaire.fantwodeck.fantwodeck
import org.combinators.solitaire.faneasy.faneasy
import org.combinators.solitaire.superflowergarden.superflowergarden
import org.combinators.solitaire.alexanderthegreat.alexanderthegreat
import org.combinators.solitaire.labellelucie.labellelucie
import org.combinators.solitaire.shamrocks.shamrocks
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}
import org.combinators.solitaire.trefoil.trefoil

/***
abstract class FanVariationController(web: WebJarsUtil, app: ApplicationLifecycle) extends InhabitationController(web, app) with RoutingEntries {
***/
trait FanVariationT extends SolitaireSolution {

  lazy val repository = new FanDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  // if a variation required extra code, just add on, like...  ++ Synthesizer.newTargets()
  lazy val targets = Synthesizer.allTargets(solitaire)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override lazy val routingPrefix = Some("fan")
}

object FanMain extends DefaultMain with FanVariationT {
  lazy val solitaire = fan
}

// NOTE: While this is solvable, the generic SOLVE logic doesn't take into account
// moves to/from the free cells; this needs to be fixed.
object FanFreeMain extends DefaultMain with FanVariationT {
  lazy val solitaire = fanfreepile
}

object ScotchPatienceMain extends DefaultMain with FanVariationT {
  lazy val solitaire = scotchpatience
}

object ShamrocksMain extends DefaultMain with FanVariationT {
  lazy val solitaire = shamrocks
}

//class FanTwoDeckController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
//  extends InhabitationController(webJars, applicationLifecycle) with FanVariationT {
//  override lazy val solitaire = fantwodeck
//}
object FanTwoDeckMain extends DefaultMain with FanVariationT {
  lazy val solitaire = fantwodeck
}

//class FanEasyController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
//  extends InhabitationController(webJars, applicationLifecycle) with FanVariationT {
//  override lazy val solitaire = faneasy
//}
object FanEasyDeckMain extends DefaultMain with FanVariationT {
  lazy val solitaire = faneasy
}

//class LaBelleLucieController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
//  extends InhabitationController(webJars, applicationLifecycle) with FanVariationT {
//  override lazy val solitaire = labellelucie
//}
object LaBelleLucieMain extends DefaultMain with FanVariationT {
  lazy val solitaire = labellelucie
}

//class SuperFlowerGardenController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
//  extends InhabitationController(webJars, applicationLifecycle) with FanVariationT {
//  override lazy val solitaire = superflowergarden
//}
object SuperFlowerGardenMain extends DefaultMain with FanVariationT {
  lazy val solitaire = superflowergarden
}

//class TrefoilController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
//  extends InhabitationController(webJars, applicationLifecycle) with FanVariationT {
//  override lazy val solitaire = trefoil
//}
object TrefoilMain extends DefaultMain with FanVariationT {
  lazy val solitaire = trefoil
}

//class AlexanderTheGreatController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
//  extends InhabitationController(webJars, applicationLifecycle) with FanVariationT {
//  override lazy val solitaire = alexanderthegreat
//}
object AlexanderTheGreatMain extends DefaultMain with FanVariationT {
  lazy val solitaire = alexanderthegreat
}
