package org.combinators.solitaire.minimal

import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.git._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import org.combinators.solitaire.shared.cls.Synthesizer
import org.combinators.solitaire.shared.compilation.{DefaultMain, SolitaireSolution}
import org.combinators.templating.persistable.JavaPersistable._
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle

import javax.inject.Inject

// SINGLE card moving
trait MinimalT extends SolitaireSolution {
  // THESE ALL HAVE TO BE LAZY VAL ...
  lazy val repository = new MinimalDomain(solitaire) with controllers with singleCardMovers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results = EmptyInhabitationBatchJobResults(Gamma)
    .addJobs[CompilationUnit](targets).compute()

  override lazy val routingPrefix = Some("minimal")
}

// MULTI card moving
trait MinimalMultiT extends SolitaireSolution {
  // THESE ALL HAVE TO BE LAZY VAL ...
  lazy val repository = new MinimalDomain(solitaire) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results = EmptyInhabitationBatchJobResults(Gamma)
    .addJobs[CompilationUnit](targets).compute()

  override lazy val routingPrefix = Some("minimal")
}


// modify routes accordingly and then you can inspect entire repository to see what is missing
class Minimal @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries {
  lazy val solitaire = minimalS

  // THESE ALL HAVE TO BE LAZY VAL ...
  lazy val repository = new MinimalDomain(solitaire) with controllers with singleCardMovers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), solitaire)

  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val targets: Seq[Constructor] = Synthesizer.allTargets(solitaire)

  lazy val results = EmptyInhabitationBatchJobResults(Gamma)
    .addJobs[CompilationUnit](targets).compute()

  lazy val controllerAddress: String = solitaire.name.toLowerCase
}

// Match the Trait with single card moves with the model that defines single card moves
object MinimalSingleMain extends DefaultMain with MinimalT {
  override lazy val solitaire = minimalS
}

// Match the Trait with multi card moves with the model that defines multi card moves
object MinimalMultiMain extends DefaultMain with MinimalMultiT {
  override lazy val solitaire = minimalM
}