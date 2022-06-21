package org.combinators.solitaire.delta

import com.github.javaparser.ast.CompilationUnit

import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, Results}
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}
import org.combinators.templating.persistable.JavaPersistable._

trait DeltaVariationT extends SolitaireSolution {

  lazy val repository = new DeltaDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()
}

object BakersDozenMain extends DefaultMain with DeltaVariationT {
  override lazy val solitaire = delta
}
