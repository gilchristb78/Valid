package example

import javax.inject.Inject
import _root_.java.nio.file.Paths

import de.tu_dortmund.cs.ls14.twirl.Python
import de.tu_dortmund.cs.ls14.cls.interpreter.{ReflectedRepository, combinator}
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import org.webjars.play.WebJarsUtil

class PythonExample @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

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
  }

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new Repository {}
  lazy val Gamma:ReflectedRepository[Repository] = ReflectedRepository(repository, classLoader = this.getClass.getClassLoader)

  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs =
    Gamma.InhabitationBatchJob[Python]('Program)

  lazy val results:Results = Results.add(jobs.run(), Paths.get("test.py"))

}