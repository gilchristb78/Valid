package example

import javax.inject.Inject
import _root_.java.nio.file.Paths

import org.combinators.templating.twirl.Python
import org.combinators.cls.interpreter.{ReflectedRepository, combinator}
import org.combinators.cls.types.syntax._
import org.combinators.cls.git.{EmptyResults, InhabitationController, Results}
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle

class PythonExample @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) {

  trait Repository {

    @combinator object makeMain {
      def apply(mainBlock: Python): Python =
        Python(s"""
                  |if __name__ == "__main__":
                  |${mainBlock.indent.getCode}
                """.stripMargin)
      val semanticType = 'MainCode =>: 'Program
    }

    @combinator object SayHello {
      def apply(toWhom: Python):  Python =
        Python(s"""print("Hello, ${toWhom.getCode}!")""")
      val semanticType = 'Person =>: 'MainCode
    }

    @combinator object Programmer {
      def apply: Python = Python("programmer")
      val semanticType = 'Person
    }

    @combinator object ByTemplate {
      def apply(mainBlock: Python): Python =
        py.test.render(mainBlock)
      val semanticType = 'MainCode =>: 'Program
    }

    @combinator object PT {
      def apply: String =
        """|This is a test
           |aslkdjalksjd
           |SLKDJSLKDJ""".stripMargin

      val semanticType = 'PersistThis
    }
  }

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new Repository {}
  lazy val Gamma:ReflectedRepository[Repository] = ReflectedRepository(repository, classLoader = this.getClass.getClassLoader)

  lazy val combinatorComponents = Gamma.combinatorComponents


  lazy val sjobs =
    Gamma.InhabitationBatchJob[String]('PersistThis)

  lazy val results:Results = EmptyResults().add(sjobs.run(), Paths.get("a","b","c","sample"))

//
//  lazy val jobs =
//    Gamma.InhabitationBatchJob[Python]('Program)
//
//  lazy val results:Results = EmptyResults().add(jobs.run(), Paths.get("a","b","c","test.py"))


}