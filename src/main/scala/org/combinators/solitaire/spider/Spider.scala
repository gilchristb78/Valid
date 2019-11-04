package org.combinators.solitaire.spider

import javax.inject.Inject
import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, InhabitationController, Results, RoutingEntries}
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.domain.Solitaire
import org.combinators.solitaire.shared.cls.Synthesizer
import org.webjars.play.WebJarsUtil
import org.combinators.templating.persistable.JavaPersistable._
import play.api.inject.ApplicationLifecycle
import org.combinators.solitaire.spider.spider
import org.combinators.solitaire.spiderette.spiderette
import org.combinators.solitaire.scorpion.scorpion
import org.combinators.solitaire.mrsmop.mrsmop
import org.combinators.solitaire.gigantic.gigantic
import org.combinators.solitaire.spiderwort.spiderwort
import org.combinators.solitaire.baby.baby
import org.combinators.solitaire.openspider.openspider
import org.combinators.solitaire.openscorpion.openscorpion
import org.combinators.solitaire.curdsandwhey.curdsandwhey



class SpiderVariationController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries {

  // request a specific variation via "http://localhost:9000/spider/SUBVAR-NAME
  lazy val variation:Solitaire = spider

  lazy val repository = new gameDomain(variation) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), variation)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(variation)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override val routingPrefix = Some("spider")
  lazy val controllerAddress: String = variation.name.toLowerCase
}

class SpiderController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends SpiderVariationController(webJars, applicationLifecycle) {
  val s0 = System.nanoTime()
  override lazy val variation = spider
  val s1 = System.nanoTime()
  println("---BASE SPIDER TIME: " + (s1-s0) + " ns---")

}


class SpideretteController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends SpiderVariationController(webJars, applicationLifecycle) {
  override lazy val variation = spiderette
}

class ScorpionController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends SpiderVariationController(webJars, applicationLifecycle) {
  val c0 = System.nanoTime()
  override lazy val variation = scorpion
  val c1 = System.nanoTime()
  println("---SCORPION SPIDER FAN TIME: " + (c1-c0) + " ns---")

}

class MrsMopController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends SpiderVariationController(webJars, applicationLifecycle) {
  override lazy val variation = mrsmop
}

class GiganticController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends SpiderVariationController(webJars, applicationLifecycle) {
  override lazy val variation = gigantic
}

class SpiderwortController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends SpiderVariationController(webJars, applicationLifecycle) {
  override lazy val variation = spiderwort
}

class BabyController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends SpiderVariationController(webJars, applicationLifecycle) {
  override lazy val variation = baby
}

class OpenSpiderController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends SpiderVariationController(webJars, applicationLifecycle) {
  override lazy val variation = openspider
}

class OpenScorpionController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends SpiderVariationController(webJars, applicationLifecycle) {
  override lazy val variation = openscorpion
}

class CurdsAndWheyController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends SpiderVariationController(webJars, applicationLifecycle) {
  val w0 = System.nanoTime()
  override lazy val variation = curdsandwhey
  val w1 = System.nanoTime()
  println("---CURDS SPIDER TIME: " + (w1-w0) + " ns---")
}