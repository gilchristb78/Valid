package example

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import controllers.WebJarAssets
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.webjars.play.RequireJS


class Example @Inject()(webJars: WebJarAssets, requireJS: RequireJS) extends InhabitationController(webJars, requireJS) {

  // Start by defining repository of combinators
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new SomeCombinators with OtherCombinators {}

  // not needed yet since we do not have dynamic combinators
  lazy val Gamma = ReflectedRepository(repository, classLoader = this.getClass.getClassLoader)

  /** This needs to be defined, and it is set from Gamma. */
  lazy val combinators = Gamma.combinators

  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit]('TemperatureInterface)

//  Add more jobs as necessary
      //.addJob[CompilationUnit](SEMANTIC-TYPE)

  lazy val results = Results.addAll(jobs.run())

}
