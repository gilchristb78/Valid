package org.combinators.solitaire.castle

import javax.inject.Inject
import org.webjars.play.WebJarsUtil
import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git._
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.domain.Solitaire
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}
import org.combinators.templating.persistable.JavaPersistable._
import play.api.inject.ApplicationLifecycle

trait CastleT extends SolitaireSolution {
  val solitaire = castle //  new domain.castle.Domain()

  /** KlondikeDomain for Castle defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new CastleDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)
  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

}

class Castle @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries with CastleT {

}


object CastleMain extends CastleT with DefaultMain {
  print ("Good Luck!")
}
