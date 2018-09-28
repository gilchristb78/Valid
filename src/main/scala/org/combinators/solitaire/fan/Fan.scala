package org.combinators.solitaire.fan

import com.github.javaparser.ast.CompilationUnit
import javax.inject.Inject
import org.combinators.cls.git._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.templating.persistable.JavaPersistable._
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle
import domain.fan


abstract class FanVariationController(web: WebJarsUtil, app: ApplicationLifecycle) extends InhabitationController(web, app) with RoutingEntries {

  val variation: domain.fan.Domain

  lazy val repository = new FanDomain(variation) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), variation)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets = Synthesizer.allTargets(variation)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override val routingPrefix = Some("fan")
  lazy val controllerAddress: String = variation.name.toLowerCase

}

class FanController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FanVariationController(webJars, applicationLifecycle) {
  lazy val variation = new fan.Domain
}

class FanFreePileController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FanVariationController(webJars, applicationLifecycle) {
  lazy val variation = new fan.FanFreePile
}
