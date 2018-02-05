package org.combinators.solitaire.klondike

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import domain.klondike
import domain.klondike._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.syntax._
import org.combinators.cls.git._
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.templating.persistable.JavaPersistable._
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle
import play.api.routing.SimpleRouter


// domain
import domain._


abstract class KlondikeVariationController(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries {

  // request a specific variation via "http://localhost:9000/klondike/
  val variation: klondike.KlondikeDomain

  /** KlondikeDomain for Klondike defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new KlondikeDomain(variation) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), variation)
  lazy val combinatorComponents = Gamma.combinatorComponents


  lazy val results:Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](Synthesizer.allTargets(variation)).compute()

  override val routingPrefix: Option[String] = Some("klondike")
  lazy val controllerAddress: String = variation.name.toLowerCase
}

class KlondikeController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = new klondike.KlondikeDomain()
}

class WhiteheadController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = new klondike.Whitehead()
}

class EastcliffController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = new klondike.EastCliff()
}

class SmallHarpController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = new klondike.SmallHarp()
}

class ThumbAndPouchController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = new klondike.ThumbAndPouchKlondikeDomain
}

class DealByThreeController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = new klondike.DealByThreeKlondikeDomain()
}

class EastHavenController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = new klondike.EastHaven()
}

class DoubleEastHavenController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = new klondike.DoubleEastHaven()
}

class TripleEastHavenController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = new klondike.TripleEastHaven()
}
