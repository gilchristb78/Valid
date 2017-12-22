package example

import java.nio.file.{Path, Paths}
import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import org.combinators.templating.persistable.Persistable
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.syntax._
import org.combinators.cls.git.{EmptyResults, InhabitationController}
import org.combinators.templating.persistable.JavaPersistable._
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle


class Example @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) {

  // Start by defining repository of combinators
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new SomeCombinators with OtherCombinators {}



  // not needed yet since we do not have dynamic combinators
  // kinding is just a field access.
  lazy val Gamma = ReflectedRepository(repository, classLoader = this.getClass.getClassLoader, substitutionSpace=repository.kindingSpecial.merge(repository.kindingAnother), semanticTaxonomy=repository.taxonomySpecial.merge(repository.taxonomyAnother))

  /** This needs to be defined, and it is set from Gamma. */
  lazy val combinatorComponents = Gamma.combinatorComponents

  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit]('TemperatureInterface('Temperature))
      .addJob[Int]('ReasonOfTheUniverse) // this will be found
      .addJob[Int]('ReasonOfTheUniverse :&: 'ICanUnderstand) // this wont
  //  Add more jobs as necessary
  //.addJob[CompilationUnit](SEMANTIC-TYPE)

  /**
   * Do this to store an arbitrary data type to disk.
   * The type goes into Persistable.Aux[ <here> ] and the type T attribute of Persistable.
   * Raw text has to be a string representation of the element and path the path in the Git.
   */
  implicit def PersistInt: Persistable.Aux[Int] = new Persistable {
    override def path(elem: Int): Path = Paths.get(s"TheNumber_${elem}.txt")
    override def rawText(elem: Int): Array[Byte] = elem.toString.getBytes
    override type T = Int
  }
  lazy val results = EmptyResults().addAll(jobs.run())

}
