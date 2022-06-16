package org.combinators.solitaire.gamma

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.Expression
import example.temperature.Concepts
import org.apache.commons.io.FileUtils

import javax.inject.Inject
import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, InhabitationController, ResultLocation, Results, RoutingEntries}
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.domain.Solitaire
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle
import org.combinators.templating.persistable.JavaPersistable._

import java.nio.file.{Files, Path, Paths}
import scala.concurrent.Future
import scala.util.Try

trait GammaT extends SolitaireSolution {
  val solitaire: Solitaire = gamma

  /** The computed result location (root/sourceDirectory) */
  implicit val resultLocation: ResultLocation

  lazy val repository = new gammaDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()
}

object GammaMain extends GammaT with DefaultMain {
  print ("Completed")
}

class Gamma @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries with GammaT {

}