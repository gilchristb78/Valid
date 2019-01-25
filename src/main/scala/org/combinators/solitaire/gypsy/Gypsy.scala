package org.combinators.solitaire.gypsy

import com.github.javaparser.ast.CompilationUnit
import javax.inject.Inject
import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, InhabitationController, Results, RoutingEntries}
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.solitaire.domain.Solitaire
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.templating.persistable.JavaPersistable._
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle
import org.combinators.solitaire.giant.giant
import org.combinators.solitaire.nomad.nomad
import org.combinators.solitaire.easthaven.easthaven
import org.combinators.solitaire.irmgard.irmgard
import org.combinators.solitaire.milligancell.milligancell

class GypsyVariationController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries {

  // request a specific variation via "http://localhost:9000/Gypsy/SUBVAR-NAME
  lazy val variation:Solitaire = gypsy

  lazy val repository = new gypsyDomain(variation) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), variation)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets = Synthesizer.allTargets(variation)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override val routingPrefix = Some("Gypsy")
  lazy val controllerAddress: String = variation.name.toLowerCase
}

class GypsyController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends GypsyVariationController(webJars, applicationLifecycle) {
  override lazy val variation = gypsy
}
class GiantController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends GypsyVariationController(webJars, applicationLifecycle) {
  override lazy val variation = giant
}
class NomadController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends GypsyVariationController(webJars, applicationLifecycle) {
  override lazy val variation = nomad
}
class EastHavenController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends GypsyVariationController(webJars, applicationLifecycle) {
  override lazy val variation = easthaven
}
class IrmgardController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends GypsyVariationController(webJars, applicationLifecycle) {
  override lazy val variation = irmgard
}
class MilliganCellController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends GypsyVariationController(webJars, applicationLifecycle) {
  override lazy val variation = milligancell
}