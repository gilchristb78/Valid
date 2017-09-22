package example

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.webjars.play.WebJarsUtil


class Example @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  // Start by defining repository of combinators
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new SomeCombinators with OtherCombinators {}

  
 
  // not needed yet since we do not have dynamic combinators
  // kinding is just a field access.
  lazy val Gamma = ReflectedRepository(repository, classLoader = this.getClass.getClassLoader, kinding=repository.kindingSpecial.merge(repository.kindingAnother), semanticTaxonomy=repository.taxonomySpecial.merge(repository.taxonomyAnother))

  /** This needs to be defined, and it is set from Gamma. */
  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit]('TemperatureInterface('Temperature))

//  Add more jobs as necessary
      //.addJob[CompilationUnit](SEMANTIC-TYPE)

  lazy val results = Results.addAll(jobs.run())

}
