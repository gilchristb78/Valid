package org.combinators.solitaire.klondike

import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git._
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}
import org.combinators.templating.persistable.JavaPersistable._

trait KlondikeVariationT extends SolitaireSolution {
  /** KlondikeDomain for Klondike defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new KlondikeDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)
  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets = Synthesizer.allTargets(solitaire)
  lazy val results:Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override lazy val routingPrefix = Some("klondike")
}

object KlondikeMain extends DefaultMain with KlondikeVariationT {
  override lazy val solitaire = klondike
}

object KlondikeDeal3Main extends DefaultMain with KlondikeVariationT {
  override lazy val solitaire = klondikeDeal3.klondike
}

object EastCliffMain extends DefaultMain with KlondikeVariationT {
  override lazy val solitaire = eastcliff.eastcliff
}

object SmallHarpMain extends DefaultMain with KlondikeVariationT {
  override lazy val solitaire = smallharp.smallharp
}

object WhiteheadMain extends DefaultMain with KlondikeVariationT {
  override lazy val solitaire = whitehead.whitehead
}

object ThumbAndPouchMain extends DefaultMain with KlondikeVariationT {
  override lazy val solitaire = thumbAndPouch.thumbandpouch
}