package org.combinators.solitaire.gypsy

import com.github.javaparser.ast.CompilationUnit

import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, Results}
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.solitaire.domain.Solitaire
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.templating.persistable.JavaPersistable._
import org.combinators.solitaire.giant.giant
import org.combinators.solitaire.nomad.nomad
import org.combinators.solitaire.easthaven.easthaven
import org.combinators.solitaire.irmgard.irmgard
import org.combinators.solitaire.milligancell.milligancell
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}

trait GypsyVariationT extends SolitaireSolution {
  // request a specific variation via "http://localhost:9000/Gypsy/SUBVAR-NAME
  lazy val solitaire:Solitaire = gypsy

  lazy val repository = new gypsyDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets = Synthesizer.allTargets(solitaire)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override lazy val routingPrefix = Some("gypsy")
}

object GypsyMain extends DefaultMain with GypsyVariationT {
  override lazy val solitaire = gypsy
}

object GiantMain extends DefaultMain with GypsyVariationT {
  override lazy val solitaire = giant
}

object NomadMain extends DefaultMain with GypsyVariationT {
  override lazy val solitaire = nomad
}

object EastHavenMain extends DefaultMain with GypsyVariationT {
  override lazy val solitaire = easthaven
}

object IrmgardMain extends DefaultMain with GypsyVariationT {
  override lazy val solitaire = irmgard
}

object MilliganMain extends DefaultMain with GypsyVariationT {
  override lazy val solitaire = milligancell
}