package example.temperature

import javax.inject.Inject
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.Expression
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.syntax._
import org.combinators.cls.git.{EmptyResults, InhabitationController}
import org.combinators.templating.persistable.JavaPersistable._
import org.combinators.templating.persistable.PythonWithPathPersistable._
import org.combinators.templating.persistable.PythonWithPath
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle

class Temperature @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) {

  // Note: This will produce two variations; only the first is deemed accurate, and it is interesting
  // to consider how to deny the synthesis of the second one...
  lazy val repository = new Concepts {}
  import repository._
  lazy val Gamma = ReflectedRepository(repository, substitutionSpace=precisions.merge(units), semanticTaxonomy=taxonomyLoss,
    classLoader = this.getClass.getClassLoader)
  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val jobs = Gamma.InhabitationBatchJob[CompilationUnit](artifact(artifact.api) :&: precision(precision.floating))
    .addJob[CompilationUnit](artifact(artifact.impl) :&: precision(precision.integer) :&: unit(unit.celsius))
    .addJob[PythonWithPath](sample())

  lazy val results = EmptyResults().addAll(jobs.run())

  // Omega is like Object -- the base type everything inherits from
  //Gamma.inhabit[JType](precision(Omega))
}

// sample code showing how to directly invoke, without web service.
object Manual {

  def main(args: Array[String]): Unit = {
    lazy val repository = new Concepts {}
    import repository._
    lazy val Gamma = ReflectedRepository(repository, substitutionSpace=precisions.merge(units), semanticTaxonomy=taxonomyLoss,
      classLoader = this.getClass.getClassLoader)
    println("Expressions that return Fahrenheit")
    Gamma.inhabit[Expression](artifact(artifact.compute) :&: precision(precision.integer) :&: unit(unit.celsius))
      .interpretedTerms.values.flatMap(_._2)
      .foreach(exp => println(exp))

  }
}