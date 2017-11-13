package example.temperature

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Expression, SimpleName}
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
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
  lazy val Gamma = ReflectedRepository(repository, semanticTaxonomy=taxonomyScales)
  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs = Gamma.InhabitationBatchJob[CompilationUnit](artifact(artifact.interface) :&: precision.floating)
  lazy val results = Results.addAll(jobs.run())

}

object Manual {

  def main(args: Array[String]): Unit = {
    lazy val repository = new Concepts {}
    import repository._
    lazy val Gamma = ReflectedRepository(repository, semanticTaxonomy=taxonomyScales)

    println ("Expressions that return fahrenheit")
    val it = Gamma.inhabit[Expression](artifact(artifact.expression) :&: precision.floating :&: scale.fahrenheit).interpretedTerms.values.toIterator
    while (it.hasNext) {
      val inhab = it.next
      if (inhab._2.nonEmpty)
        println ("result:" + inhab._2.head.toString)
    }

    println ("Expressions that return arbitrary temperatures")
    val it2 = Gamma.inhabit[Expression](artifact(artifact.expression) :&: precision.floating).interpretedTerms.values.toIterator
    while (it2.hasNext) {
      val inhab = it2.next
      if (inhab._2.nonEmpty)
        println ("result2:" + inhab._2.head.toString)
    }

    println ("Artifacts that return arbitrary temperatures")
    val it3 = Gamma.inhabit[Expression](artifact(artifact.interface)).interpretedTerms.values.toIterator
    while (it3.hasNext) {
      val inhab = it3.next
      if (inhab._2.nonEmpty)
        println ("result2:" + inhab._2.head.toString)
    }
  }
}