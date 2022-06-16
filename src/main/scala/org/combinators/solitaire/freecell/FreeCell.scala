package org.combinators.solitaire.freecell

import javax.inject.Inject
import org.webjars.play.WebJarsUtil
import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git._
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.domain.Solitaire
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}
import play.api.inject.ApplicationLifecycle
import org.combinators.templating.persistable.JavaPersistable._
/***
abstract class FreeCellVariationController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries  {
***/
trait FreeCellVariationT extends SolitaireSolution {

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new gameDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override lazy val routingPrefix: Option[String] = Some("freecell")
}

class FreeCellController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends InhabitationController(webJars, applicationLifecycle) with FreeCellVariationT {

  lazy val solitaire = freecell // new freeCell.FreeCellDomain
}

object FreeCellMain extends DefaultMain with FreeCellVariationT {
  lazy val solitaire = freecell // new freeCell.FreeCellDomain
  print ("Completed")
}

class ChallengeFreeCellController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends InhabitationController(webJars, applicationLifecycle) with FreeCellVariationT {
  lazy val solitaire = freecell //  new freeCell.ChallengeFreeCell
}

class SuperChallengeFreeCellController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends InhabitationController(webJars, applicationLifecycle) with FreeCellVariationT {
  lazy val solitaire = freecell //  new freeCell.SuperChallengeFreeCell
}

class ForeCellController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends InhabitationController(webJars, applicationLifecycle) with FreeCellVariationT {
  lazy val solitaire = freecell //  new freeCell.ForeCell
}

class DoubleFreeCellController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends InhabitationController(webJars, applicationLifecycle) with FreeCellVariationT {
  lazy val solitaire = freecell //  new freeCell.DoubleFreeCell
}

class StalactitesController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends InhabitationController(webJars, applicationLifecycle) with FreeCellVariationT {
  lazy val solitaire = freecell //  new freeCell.DoubleFreeCell
}
