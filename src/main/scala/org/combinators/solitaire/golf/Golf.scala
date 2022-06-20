package org.combinators.solitaire.golf

import com.github.javaparser.ast.CompilationUnit

import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, Results}
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.templating.persistable.JavaPersistable._
import org.combinators.solitaire.golf_no_wrap.golf_no_wrap
import org.combinators.solitaire.allInARow.allInARow
import org.combinators.solitaire.flake.flake
import org.combinators.solitaire.flake_two_decks.flake_two_decks
import org.combinators.solitaire.robert.robert
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}

/***
class GolfVariationController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries {
***/
trait GolfVariationT extends SolitaireSolution {

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new golfDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override lazy val routingPrefix:Option[String] = Some("golf")
}

object GolfMain extends DefaultMain with GolfVariationT {
  override lazy val solitaire = golf
}

object GolfNoWrapMain extends DefaultMain with GolfVariationT {
  override lazy val solitaire = golf_no_wrap
}

object AllInARowMain extends DefaultMain with GolfVariationT {
  override lazy val solitaire = allInARow
}

object FlakeMain extends DefaultMain with GolfVariationT {
  override lazy val solitaire = flake
}

object FlakeTwoDecksMain extends DefaultMain with GolfVariationT {
  override lazy val solitaire = flake_two_decks
}

object RobertMain extends DefaultMain with GolfVariationT {
  override lazy val solitaire = robert
}

