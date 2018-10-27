package org.combinators.solitaire.freecell

import javax.inject.Inject
import org.webjars.play.WebJarsUtil
import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git._
import org.combinators.solitaire.domain.Solitaire
import org.combinators.solitaire.shared.cls.Synthesizer
import play.api.inject.ApplicationLifecycle
import org.combinators.templating.persistable.JavaPersistable._

abstract class FreeCellVariationController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries  {

  val variation: Solitaire

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new gameDomain(variation) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), variation)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val results:Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](Synthesizer.allTargets(variation)).compute()


  override val routingPrefix: Option[String] = Some("freecell")
  lazy val controllerAddress: String = variation.name.toLowerCase
}

class FreeCellController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FreeCellVariationController(webJars, applicationLifecycle) {
  lazy val variation = freecell // new freeCell.FreeCellDomain
}

class ChallengeFreeCellController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FreeCellVariationController(webJars, applicationLifecycle) {
  lazy val variation = freecell //  new freeCell.ChallengeFreeCell
}

class SuperChallengeFreeCellController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FreeCellVariationController(webJars, applicationLifecycle) {
  lazy val variation = freecell //  new freeCell.SuperChallengeFreeCell
}

class ForeCellController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FreeCellVariationController(webJars, applicationLifecycle) {
  lazy val variation = freecell //  new freeCell.ForeCell
}

class DoubleFreeCellController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FreeCellVariationController(webJars, applicationLifecycle) {
  lazy val variation = freecell //  new freeCell.DoubleFreeCell
}

class StalactitesController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends FreeCellVariationController(webJars, applicationLifecycle) {
  lazy val variation = freecell //  new freeCell.Stalactites
}
