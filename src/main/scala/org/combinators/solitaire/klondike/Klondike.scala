package org.combinators.solitaire.klondike

import javax.inject.Inject
import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git._
import org.combinators.solitaire.domain.Solitaire
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.templating.persistable.JavaPersistable._
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle


abstract class KlondikeVariationController(web: WebJarsUtil, app: ApplicationLifecycle)
  extends InhabitationController(web, app) with RoutingEntries {

  // request a specific variation via "http://localhost:9000/klondike/SUBVAR-NAME
  val variation: Solitaire // klondike.KlondikeDomain

  /** KlondikeDomain for Klondike defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new KlondikeDomain(variation) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), variation)
  lazy val combinatorComponents = Gamma.combinatorComponents

  val targets = Synthesizer.allTargets(variation)
  lazy val results:Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override val routingPrefix = Some("klondike")
  val controllerAddress: String = variation.name.toLowerCase
}

class KlondikeController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = klondike // new klondike.KlondikeDomain
}

class WhiteheadController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = klondike // new klondike.Whitehead
}

class EastcliffController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = klondike // new klondike.EastCliff
}

class SmallHarpController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = klondike // new klondike.SmallHarp
}

class ThumbAndPouchController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = klondike // new klondike.ThumbAndPouchKlondikeDomain
}

class DealByThreeController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = klondike // new klondike.DealByThreeKlondikeDomain
}

class EastHavenController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = klondike //  new klondike.EastHaven
}

class DoubleEastHavenController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = klondike //  new klondike.DoubleEastHaven
}

class TripleEastHavenController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = klondike // new klondike.TripleEastHaven
}
