
package org.combinators.solitaire.shared.java

import org.combinators.templating.twirl.Java
import org.combinators.templating.twirl.Python
import com.github.javaparser.ast._
import com.github.javaparser.ast.body._
import com.github.javaparser.ast.comments._
import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import com.github.javaparser.ast.`type`._

object DomainInit extends _root_.play.twirl.api.BaseScalaTemplate[org.combinators.templating.twirl.JavaFormat.Appendable,_root_.play.twirl.api.Format[org.combinators.templating.twirl.JavaFormat.Appendable]](org.combinators.templating.twirl.JavaFormat) with _root_.play.twirl.api.Template4[Seq[Statement],Seq[Statement],Seq[Statement],Seq[Statement],org.combinators.templating.twirl.JavaFormat.Appendable] {

  /**/
  def apply/*1.2*/(ModelInit: Seq[Statement], ViewInit: Seq[Statement], ControlInit : Seq[Statement], SetupInitialState : Seq[Statement]):org.combinators.templating.twirl.JavaFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.121*/("""

"""),format.raw/*3.1*/("""// Fields
CardImages ci = getCardImages();

int cw = ci.getWidth();
int ch = ci.getHeight();

"""),_display_(/*9.2*/Java(ModelInit)),format.raw/*9.17*/("""

"""),_display_(/*11.2*/Java(ViewInit)),format.raw/*11.16*/("""

"""),_display_(/*13.2*/Java(ControlInit)),format.raw/*13.19*/("""

"""),_display_(/*15.2*/Java(SetupInitialState)))
      }
    }
  }

  def render(ModelInit:Seq[Statement],ViewInit:Seq[Statement],ControlInit:Seq[Statement],SetupInitialState:Seq[Statement]): org.combinators.templating.twirl.JavaFormat.Appendable = apply(ModelInit,ViewInit,ControlInit,SetupInitialState)

  def f:((Seq[Statement],Seq[Statement],Seq[Statement],Seq[Statement]) => org.combinators.templating.twirl.JavaFormat.Appendable) = (ModelInit,ViewInit,ControlInit,SetupInitialState) => apply(ModelInit,ViewInit,ControlInit,SetupInitialState)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: Tue Oct 04 13:18:10 EDT 2022
                  SOURCE: /home/ben/IdeaProjects/nextgen-solitaire/src/main/java-templates/org/combinators/solitaire/shared/DomainInit.scala.java
                  HASH: 0c4b25f40f74a28f44b4bc149124a6dc9620729f
                  MATRIX: 815->1|1048->120|1076->122|1196->217|1231->232|1260->235|1295->249|1324->252|1362->269|1391->272
                  LINES: 16->1|21->1|23->3|29->9|29->9|31->11|31->11|33->13|33->13|35->15
                  -- GENERATED --
              */
          