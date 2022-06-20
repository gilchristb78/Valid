package org.combinators.solitaire.simplesimon

import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, Results}
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}
import org.combinators.templating.persistable.JavaPersistable._
import org.combinators.solitaire.simplevar.simplevar

/***
class SimplesimonVariationController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries {
***/
trait SimpleSimonVariationT extends SolitaireSolution {

  lazy val repository = new simplesimonDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override lazy val routingPrefix = Some("simplesimon")
}

object SimpleSimonMain extends DefaultMain with SimpleSimonVariationT {
  override lazy val solitaire = simplesimon
}

object SimpleVarMain extends DefaultMain with SimpleSimonVariationT {
  override lazy val solitaire = simplevar
}
