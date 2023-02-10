
package py

import org.combinators.templating.twirl.Java
import org.combinators.templating.twirl.Python
import com.github.javaparser.ast._
import com.github.javaparser.ast.body._
import com.github.javaparser.ast.comments._
import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import com.github.javaparser.ast.`type`._

object test extends _root_.play.twirl.api.BaseScalaTemplate[org.combinators.templating.twirl.PythonFormat.Appendable,_root_.play.twirl.api.Format[org.combinators.templating.twirl.PythonFormat.Appendable]](org.combinators.templating.twirl.PythonFormat) with _root_.play.twirl.api.Template1[Python,org.combinators.templating.twirl.PythonFormat.Appendable] {

  /**/
  def apply/*1.2*/(sayHello: Python):org.combinators.templating.twirl.PythonFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.20*/("""

"""),format.raw/*3.1*/("""# this is the second one
if __name__ == "__main__":
"""),_display_(/*5.2*/sayHello/*5.10*/.indent))
      }
    }
  }

  def render(sayHello:Python): org.combinators.templating.twirl.PythonFormat.Appendable = apply(sayHello)

  def f:((Python) => org.combinators.templating.twirl.PythonFormat.Appendable) = (sayHello) => apply(sayHello)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: Tue Oct 04 13:18:10 EDT 2022
                  SOURCE: /home/ben/IdeaProjects/nextgen-solitaire/src/main/python-templates/test.scala.py
                  HASH: 56ffa881264ff1d0c178f17a8b6696211ebc35fc
                  MATRIX: 729->1|862->19|890->21|968->74|984->82
                  LINES: 16->1|21->1|23->3|25->5|25->5
                  -- GENERATED --
              */
          