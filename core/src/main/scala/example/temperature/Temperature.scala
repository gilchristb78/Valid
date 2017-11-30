package example.temperature

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
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
  lazy val jobs = Gamma
      .InhabitationBatchJob[CompilationUnit](artifact(artifact.api) :&: precision(precision.floating))
      .addJob[CompilationUnit](artifact(artifact.impl) :&: unit(unit.fahrenheit) :&: precision(precision.integer))

  // put all generated files into locally constructed git repository
  lazy val results = Results.addAll(jobs.run())
}

// sample code showing how to directly invoke, without web service.
object Manual {

  def main(args: Array[String]): Unit = {
    lazy val repository = new Concepts {}
    import repository._
    lazy val Gamma = ReflectedRepository(repository, kinding=precisions.merge(units), semanticTaxonomy=taxonomyLoss,
      classLoader = this.getClass.getClassLoader)

    println("Generate both classes")
    Seq(
      Gamma.inhabit[CompilationUnit](artifact(artifact.impl) :&: unit(unit.celsius) :&: precision(precision.floating)),
      Gamma.inhabit[CompilationUnit](artifact(artifact.api))
    ).foreach(comp =>
        comp.interpretedTerms.values.flatMap(_._2)
          .foreach(exp => println(exp))
    )

    println("Generate int adapter")
    Gamma.inhabit[CompilationUnit](artifact(artifact.impl) :&: unit(unit.celsius) :&: precision(precision.integer))
        .interpretedTerms.values.flatMap(_._2)
        .foreach(exp => println(exp))

    println("Generate int fahnrenheit adapter")
    Gamma.inhabit[CompilationUnit](artifact(artifact.impl) :&: unit(unit.fahrenheit) :&: precision(precision.integer))
      .interpretedTerms.values.flatMap(_._2)
      .foreach(exp => println(exp))
  }
}


// Omega is like Object -- the base type everything inherits from
//Gamma.inhabit[JType](precision(Omega))