package org.combinators.solitaire.napoleon

import com.github.javaparser.ast.CompilationUnit

import org.combinators.cls.git._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}
import org.combinators.templating.persistable.JavaPersistable._

trait NapoleonT extends SolitaireSolution {

  lazy val repository = new NapoleonDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override lazy val routingPrefix = Some("napoleon")
}

// Match the Trait with multi card moves with the model that defines multi card moves
object NapoleonMain extends DefaultMain with NapoleonT {
  override lazy val solitaire = napoleon
}
