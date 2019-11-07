package org.combinators.solitaire.simplesimon

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
import org.combinators.solitaire.simplevar.simplevar

class SimplesimonVariationController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries {

  lazy val variation:Solitaire = simplesimon

  // Domain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new simplesimonDomain(variation) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), variation)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(variation)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override val routingPrefix = Some("simplesimon")
  lazy val controllerAddress: String = variation.name.toLowerCase
}
class SimplesimonController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends SimplesimonVariationController(webJars, applicationLifecycle) {
  override lazy val variation = simplesimon
}
class SimplevarController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends SimplesimonVariationController(webJars, applicationLifecycle) {
  override lazy val variation = simplevar
}
