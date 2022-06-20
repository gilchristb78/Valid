package org.combinators.solitaire.spider

import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, Results}
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.templating.persistable.JavaPersistable._
import org.combinators.solitaire.spiderette.spiderette
import org.combinators.solitaire.scorpion.scorpion
import org.combinators.solitaire.mrsmop.mrsmop
import org.combinators.solitaire.gigantic.gigantic
import org.combinators.solitaire.spiderwort.spiderwort
import org.combinators.solitaire.baby.baby
import org.combinators.solitaire.openspider.openspider
import org.combinators.solitaire.openscorpion.openscorpion
import org.combinators.solitaire.curdsandwhey.curdsandwhey
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}

trait SpiderVariationT extends SolitaireSolution {

  lazy val repository = new gameDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override lazy val routingPrefix = Some("spider")
}

object SpiderMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = spider
}

object SpideretteMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = spiderette
}

object ScorpionMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = scorpion
}

object MrsMopMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = mrsmop
}

object GiganticMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = gigantic
}

object SpiderwortMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = spiderwort
}

object BabyMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = baby
}

object OpenSpiderMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = openspider
}

object OpenScorpionMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = openscorpion
}

object CurdsAndWheyMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = curdsandwhey
}
