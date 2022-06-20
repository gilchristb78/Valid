package org.combinators.solitaire.bakersdozen
import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, Results}
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.templating.persistable.JavaPersistable._
import org.combinators.solitaire.spanish_patience.spanish_patience
import org.combinators.solitaire.castles_in_spain.castles_in_spain
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}

trait BakersDozenVariationT extends SolitaireSolution {

  lazy val repository = new gameDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets = Synthesizer.allTargets(solitaire)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override lazy val routingPrefix = Some("bakersdozen")
}

object BakersDozenMain extends DefaultMain with BakersDozenVariationT {
  override lazy val solitaire = bakersdozen
}

object SpanishPatienceMain extends DefaultMain with BakersDozenVariationT {
  override lazy val solitaire = spanish_patience
}

object CastlesInSpainMain extends DefaultMain with BakersDozenVariationT {
  override lazy val solitaire = castles_in_spain
}


