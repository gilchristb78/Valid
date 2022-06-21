package org.combinators.solitaire.golf

import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, Results}
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.templating.persistable.JavaPersistable._
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}

trait GolfVariationT extends SolitaireSolution {
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
  override lazy val solitaire = org.combinators.solitaire.golf.no_wrap.definition
}

object AllInARowMain extends DefaultMain with GolfVariationT {
  override lazy val solitaire = org.combinators.solitaire.golf.allInARow.definition
}

object FlakeMain extends DefaultMain with GolfVariationT {
  override lazy val solitaire = org.combinators.solitaire.golf.flake.definition
}

object FlakeTwoDecksMain extends DefaultMain with GolfVariationT {
  override lazy val solitaire = org.combinators.solitaire.golf.flake_two_decks.definition
}

object RobertMain extends DefaultMain with GolfVariationT {
  override lazy val solitaire = org.combinators.solitaire.golf.robert.definition
}

