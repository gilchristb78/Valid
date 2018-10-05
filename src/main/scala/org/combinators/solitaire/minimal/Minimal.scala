package org.combinators.solitaire.minimal

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import domain.Solitaire
import org.combinators.cls.git._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.narcotic.gameDomain
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.templating.persistable.JavaPersistable._
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle

// domain
class Minimal @Inject()(web: WebJarsUtil, app: ApplicationLifecycle) extends InhabitationController(web, app) with RoutingEntries {

  val sol = minimal

  val repository = new gameDomain(sol) with controllers {}

  val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), sol)

  val combinatorComponents = Gamma.combinatorComponents

  val targets = Synthesizer.allTargets(sol)

  val results = EmptyInhabitationBatchJobResults(Gamma)
    .addJobs[CompilationUnit](targets).compute()

  val controllerAddress: String = sol.name.toLowerCase
}
