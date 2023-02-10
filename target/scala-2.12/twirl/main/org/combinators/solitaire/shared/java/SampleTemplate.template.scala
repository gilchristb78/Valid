
package org.combinators.solitaire.shared.java

import org.combinators.templating.twirl.Java
import org.combinators.templating.twirl.Python
import com.github.javaparser.ast._
import com.github.javaparser.ast.body._
import com.github.javaparser.ast.comments._
import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import com.github.javaparser.ast.`type`._

object SampleTemplate extends _root_.play.twirl.api.BaseScalaTemplate[org.combinators.templating.twirl.JavaFormat.Appendable,_root_.play.twirl.api.Format[org.combinators.templating.twirl.JavaFormat.Appendable]](org.combinators.templating.twirl.JavaFormat) with _root_.play.twirl.api.Template1[Expression,org.combinators.templating.twirl.JavaFormat.Appendable] {

  /**/
  def apply/*1.2*/(planet:Expression):org.combinators.templating.twirl.JavaFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.21*/("""

"""),format.raw/*3.1*/("""package anotherOne;

/** Example template, showing how to bring in new code. */
public class MyBad """),format.raw/*6.20*/("""{"""),format.raw/*6.21*/("""

     """),format.raw/*8.6*/("""public static void main (String[] args) """),format.raw/*8.46*/("""{"""),format.raw/*8.47*/("""
         """),format.raw/*9.10*/("""System.out.println("""),_display_(/*9.30*/Java(planet)),format.raw/*9.42*/(""");
     """),format.raw/*10.6*/("""}"""),format.raw/*10.7*/("""
"""),format.raw/*11.1*/("""}"""))
      }
    }
  }

  def render(planet:Expression): org.combinators.templating.twirl.JavaFormat.Appendable = apply(planet)

  def f:((Expression) => org.combinators.templating.twirl.JavaFormat.Appendable) = (planet) => apply(planet)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: Tue Oct 04 13:18:10 EDT 2022
                  SOURCE: /home/ben/IdeaProjects/nextgen-solitaire/src/main/java-templates/org/combinators/solitaire/shared/SampleTemplate.scala.java
                  HASH: 59d9f83b154004a3465594a4ddaccc98f5a31ea4
                  MATRIX: 770->1|902->20|930->22|1056->121|1084->122|1117->129|1184->169|1212->170|1249->180|1295->200|1327->212|1362->220|1390->221|1418->222
                  LINES: 16->1|21->1|23->3|26->6|26->6|28->8|28->8|28->8|29->9|29->9|29->9|30->10|30->10|31->11
                  -- GENERATED --
              */
          