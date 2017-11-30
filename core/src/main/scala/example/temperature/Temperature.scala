package example.temperature

import java.nio.file.{Path, Paths}
import java.util.UUID
import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.`type`.{Type => JType}
import de.tu_dortmund.cs.ls14.Persistable
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.Omega
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import de.tu_dortmund.cs.ls14.java.JavaPersistable._
import org.webjars.play.WebJarsUtil

class Temperature @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  // Note: This will produce two variations; only the first is deemed accurate, and it is interesting
  // to consider how to deny the synthesis of the second one...
  lazy val repository = new Concepts {}
  import repository._
  lazy val Gamma = ReflectedRepository(repository, kinding=precisions.merge(units), semanticTaxonomy=taxonomyLoss,
    classLoader = this.getClass.getClassLoader)
  lazy val combinatorComponents = Gamma.combinatorComponents

  /* Just for testing
  implicit val persistExp: Persistable.Aux[Expression] = new Persistable {
    type T = Expression
    def rawText(exp: Expression): String = exp.toString
    def path(exp: Expression): Path = Paths.get(UUID.randomUUID().toString)
  } */

  lazy val jobs = Gamma.InhabitationBatchJob[CompilationUnit](artifact(artifact.api) :&: precision(precision.floating))
    .addJob[CompilationUnit](artifact(artifact.impl) :&: precision(precision.integer) :&: unit(unit.celsius))

  lazy val results = Results.addAll(jobs.run())

  // Omega is like Object -- the base type everything inherits from
  //Gamma.inhabit[JType](precision(Omega))
}

// sample code showing how to directly invoke, without web service.
object Manual {

  def main(args: Array[String]): Unit = {
    lazy val repository = new Concepts {}
    import repository._
    lazy val Gamma = ReflectedRepository(repository, kinding=precisions.merge(units), semanticTaxonomy=taxonomyLoss,
      classLoader = this.getClass.getClassLoader)
    println("Expressions that return Fahrenheit")
    Gamma.inhabit[Expression](artifact(artifact.compute) :&: precision(precision.floating) :&: unit(unit.fahrenheit))
      .interpretedTerms.values.flatMap(_._2)
      .foreach(exp => println(exp))

  }
}