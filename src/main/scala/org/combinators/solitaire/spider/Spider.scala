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

//TODO Not for variations rn, just the singluar spider... see doc for saved old
//Changes: slightly to class header, variation is now named 'solitaire' and that change propogated throughout
//for repo, it's SpiderDomain not gamedomain
class Spider @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries {

  // request a specific variation via "http://localhost:9000/spider/SUBVAR-NAME
  val solitaire:Solitaire = spider

  /** SpiderDomain for Spider defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new SpiderDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  //override val routingPrefix = Some("spider")
  val controllerAddress: String = solitaire.name.toLowerCase
}
/*
class SpiderController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends SpiderVariationController(webJars, applicationLifecycle) {
  lazy val variation = new spider.SpiderDomain
}

class SpideretteController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends SpiderVariationController(webJars, applicationLifecycle) {
  lazy val variation = new spider.Spiderette
}*/