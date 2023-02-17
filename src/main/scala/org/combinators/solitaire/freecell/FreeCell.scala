package org.combinators.solitaire.freecell

import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git._
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}
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

object FreeCellMain extends DefaultMain with FreeCellVariationT {
 lazy val solitaire = regular.freecell
}

// remaining ones below need updating! In particular, model the variations that are found in the
// Java native solutions.
object ChallengeFreeCellMain extends DefaultMain with FreeCellVariationT {
  override lazy val routingPrefix: Option[String] = Some("challenge")
  lazy val solitaire = challenge.challengeFreecell
}

object SuperChallengeFreeCellMain extends DefaultMain with FreeCellVariationT {
  override lazy val routingPrefix: Option[String] = Some("superchallenge")
  lazy val solitaire = superchallenge.superchallengeFreecell
}

object ForeCellMain extends DefaultMain with FreeCellVariationT {
  override lazy val routingPrefix: Option[String] = Some("forecell")
  lazy val solitaire = forecell.forecell
}

object DoubleFreeCellMain extends DefaultMain with FreeCellVariationT {
  override lazy val routingPrefix: Option[String] = Some("doublefreecell")
  lazy val solitaire = doublefreecell.doubleFreecell
}

object StalactitesMain extends DefaultMain with FreeCellVariationT {
  override lazy val routingPrefix: Option[String] = Some("stalactites")
  lazy val solitaire = stalactites.stalactites
}