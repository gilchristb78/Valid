package org.combinators.solitaire.spider

import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, Results}
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.templating.persistable.JavaPersistable._
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
  override lazy val solitaire = spiderette.spiderette
}

object ScorpionMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = scorpion.scorpion
}

object MrsMopMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = mrsmop.mrsmop
}

object GiganticMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = gigantic.gigantic
}

object SpiderwortMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = spiderwort.spiderwort
}

object BabyMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = baby.baby
}

object OpenSpiderMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = openspider.openspider
}

object OpenScorpionMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = openscorpion.openscorpion
}

object CurdsAndWheyMain extends DefaultMain with SpiderVariationT {
  override lazy val solitaire = curdsandwhey.curdsandwhey
}
