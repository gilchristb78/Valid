package example.temperature

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Expression, SimpleName}
import com.github.javaparser.ast.`type`.{Type => JType}
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.Omega
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import de.tu_dortmund.cs.ls14.java.JavaPersistable._
import org.webjars.play.WebJarsUtil

class Temperature @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  // Start by defining repository of combinators
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.

  lazy val repository = new Concepts {}
  import repository._
  lazy val Gamma = ReflectedRepository(repository, kinding=kinding)
  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs = Gamma.InhabitationBatchJob[CompilationUnit](artifact(artifact.converter) :&: precision(precision.floating))
  lazy val results = Results.addAll(jobs.run())

}

object Manual {

  def main(args: Array[String]): Unit = {
    lazy val repository = new Concepts {}
    import repository._
    lazy val Gamma = ReflectedRepository(repository, kinding = kinding)

    println("Expressions that return fahrenheit")
    Gamma.inhabit[Expression](artifact(artifact.compute) :&: precision.floating :&: scale.fahrenheit)
      .interpretedTerms.values.flatMap(_._2)
      .foreach(exp => println(exp))


    println("Expressions that return arbitrary temperatures")

    Gamma.inhabit[Expression](artifact(artifact.compute) :&: precision.floating)
      .interpretedTerms.values.flatMap(_._2).foreach(exp => println(exp))

    println("Artifacts that return arbitrary temperatures")
    Gamma.inhabit[Expression](artifact(artifact.converter))
      .interpretedTerms.values.flatMap(_._2).foreach(exp => println(exp))

    println("Artifacts that return arbitrary precisions")
    // Omega is like Object -- the base type everything inherits from

    Gamma.inhabit[JType](precision(Omega))
      .interpretedTerms.values.flatMap(_._2).foreach(jt => println(jt))

    println ("interfaces...")
    Gamma.inhabit[CompilationUnit](artifact(artifact.converter))
      .interpretedTerms.values.flatMap(_._2).foreach (cu => println(cu))

    println ("int interfaces...")
    Gamma.inhabit[CompilationUnit](artifact(artifact.converter) :&: precision(precision.integer))
      .interpretedTerms.values.flatMap(_._2).foreach (cu => println(cu))

    // interfaces
    println ("API interfaces...")
    Gamma.inhabit[CompilationUnit](artifact(artifact.api) :&: precision(precision.floating))
      .interpretedTerms.values.flatMap(_._2).foreach (cu => println(cu))

  }
}