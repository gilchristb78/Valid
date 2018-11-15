package org.combinators.solitaire.spider

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import domain.spider
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git._
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.templating.persistable.JavaPersistable._
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle


abstract class SpiderVariationController(web: WebJarsUtil, app: ApplicationLifecycle)
  extends InhabitationController(web, app) with RoutingEntries {

  // request a specific variation via "http://localhost:9000/spider/SUBVAR-NAME
  val variation: spider.SpiderDomain

  /** SpiderDomain for Spider defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new SpiderDomain(variation) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), variation)
  lazy val combinatorComponents = Gamma.combinatorComponents

  val targets = Synthesizer.allTargets(variation)
  lazy val results:Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override val routingPrefix = Some("spider")
  val controllerAddress: String = variation.name.toLowerCase
}

class SpiderController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends SpiderVariationController(webJars, applicationLifecycle) {
  lazy val variation = new spider.SpiderDomain
}

class SpideretteController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends SpiderVariationController(webJars, applicationLifecycle) {
  lazy val variation = new spider.Spiderette
}