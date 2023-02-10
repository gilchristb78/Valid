
package org.combinators.java

import org.combinators.templating.twirl.Java
import org.combinators.templating.twirl.Python
import com.github.javaparser.ast._
import com.github.javaparser.ast.body._
import com.github.javaparser.ast.comments._
import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import com.github.javaparser.ast.`type`._

object ConstraintHelper extends _root_.play.twirl.api.BaseScalaTemplate[org.combinators.templating.twirl.JavaFormat.Appendable,_root_.play.twirl.api.Format[org.combinators.templating.twirl.JavaFormat.Appendable]](org.combinators.templating.twirl.JavaFormat) with _root_.play.twirl.api.Template3[Name,SimpleName,Seq[BodyDeclaration[_$1] forSome { 
   type _$1
}],org.combinators.templating.twirl.JavaFormat.Appendable] {

  /**/
  def apply/*1.2*/(rootPackage:Name, nameParameter:SimpleName, extraFieldsOrMethods:Seq[BodyDeclaration[_]]):org.combinators.templating.twirl.JavaFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.92*/("""

"""),format.raw/*3.1*/("""package """),_display_(/*3.10*/{Java(rootPackage)}),format.raw/*3.29*/(""".model;

import ks.common.games.Solitaire;
import ks.common.model.*;
import java.util.function.BooleanSupplier;

public class ConstraintHelper """),format.raw/*9.31*/("""{"""),format.raw/*9.32*/("""

    """),format.raw/*11.5*/("""/** Helper method for processing constraints. Uses BooleanSupplier
     * to avoid evaluating all constraints which would lead to exceptions.
     * These are now lazily evaluated. */
    public static boolean ifCompute(boolean guard, BooleanSupplier truth, BooleanSupplier falsehood) """),format.raw/*14.102*/("""{"""),format.raw/*14.103*/("""
        """),format.raw/*15.9*/("""if (guard) """),format.raw/*15.20*/("""{"""),format.raw/*15.21*/("""
            """),format.raw/*16.13*/("""return truth.getAsBoolean();
        """),format.raw/*17.9*/("""}"""),format.raw/*17.10*/(""" """),format.raw/*17.11*/("""else """),format.raw/*17.16*/("""{"""),format.raw/*17.17*/("""
            """),format.raw/*18.13*/("""return falsehood.getAsBoolean();
        """),format.raw/*19.9*/("""}"""),format.raw/*19.10*/("""
    """),format.raw/*20.5*/("""}"""),format.raw/*20.6*/("""

    """),format.raw/*22.5*/("""/** Extra solitaire-manipulating methods are inserted here. */
    """),_display_(/*23.6*/Java(extraFieldsOrMethods)),format.raw/*23.32*/("""

    """),format.raw/*25.5*/("""/** Helper to be able to retrieve variation specific solitaire without external cast. */
    public static """),_display_(/*26.20*/Java(rootPackage)),format.raw/*26.37*/("""."""),_display_(/*26.39*/Java(nameParameter)),format.raw/*26.58*/(""" """),format.raw/*26.59*/("""getVariation(Solitaire game) """),format.raw/*26.88*/("""{"""),format.raw/*26.89*/("""
        """),format.raw/*27.9*/("""return ("""),_display_(/*27.18*/Java(rootPackage)),format.raw/*27.35*/("""."""),_display_(/*27.37*/Java(nameParameter)),format.raw/*27.56*/(""") game;
    """),format.raw/*28.5*/("""}"""),format.raw/*28.6*/("""
"""),format.raw/*29.1*/("""}"""),format.raw/*29.2*/("""
"""))
      }
    }
  }

  def render(rootPackage:Name,nameParameter:SimpleName,extraFieldsOrMethods:Seq[BodyDeclaration[_$1] forSome { 
   type _$1
}]): org.combinators.templating.twirl.JavaFormat.Appendable = apply(rootPackage,nameParameter,extraFieldsOrMethods)

  def f:((Name,SimpleName,Seq[BodyDeclaration[_$1] forSome { 
   type _$1
}]) => org.combinators.templating.twirl.JavaFormat.Appendable) = (rootPackage,nameParameter,extraFieldsOrMethods) => apply(rootPackage,nameParameter,extraFieldsOrMethods)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: Tue Oct 04 13:18:10 EDT 2022
                  SOURCE: /home/ben/IdeaProjects/nextgen-solitaire/src/main/java-templates/org/combinators/ConstraintHelper.scala.java
                  HASH: a56eb7fc5f42dbbc471d5cd089bbaab945cab3bb
                  MATRIX: 811->1|1014->91|1042->93|1077->102|1116->121|1286->264|1314->265|1347->271|1661->556|1691->557|1727->566|1766->577|1795->578|1836->591|1900->628|1929->629|1958->630|1991->635|2020->636|2061->649|2129->690|2158->691|2190->696|2218->697|2251->703|2345->771|2392->797|2425->803|2560->911|2598->928|2627->930|2667->949|2696->950|2753->979|2782->980|2818->989|2854->998|2892->1015|2921->1017|2961->1036|3000->1048|3028->1049|3056->1050|3084->1051
                  LINES: 18->1|23->1|25->3|25->3|25->3|31->9|31->9|33->11|36->14|36->14|37->15|37->15|37->15|38->16|39->17|39->17|39->17|39->17|39->17|40->18|41->19|41->19|42->20|42->20|44->22|45->23|45->23|47->25|48->26|48->26|48->26|48->26|48->26|48->26|48->26|49->27|49->27|49->27|49->27|49->27|50->28|50->28|51->29|51->29
                  -- GENERATED --
              */
          