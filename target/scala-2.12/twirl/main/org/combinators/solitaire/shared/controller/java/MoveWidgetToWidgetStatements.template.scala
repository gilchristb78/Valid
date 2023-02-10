
package org.combinators.solitaire.shared.controller.java

import org.combinators.templating.twirl.Java
import org.combinators.templating.twirl.Python
import com.github.javaparser.ast._
import com.github.javaparser.ast.body._
import com.github.javaparser.ast.comments._
import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import com.github.javaparser.ast.`type`._

object MoveWidgetToWidgetStatements extends _root_.play.twirl.api.BaseScalaTemplate[org.combinators.templating.twirl.JavaFormat.Appendable,_root_.play.twirl.api.Format[org.combinators.templating.twirl.JavaFormat.Appendable]](org.combinators.templating.twirl.JavaFormat) with _root_.play.twirl.api.Template5[Name,SimpleName,SimpleName,SimpleName,SimpleName,org.combinators.templating.twirl.JavaFormat.Appendable] {

  /**/
  def apply/*1.2*/(RootPackage: Name,
        TheMove: SimpleName,
        MovingWidgetName: SimpleName,
        SourceWidgetName: SimpleName,
        TargetWidgetName: SimpleName):org.combinators.templating.twirl.JavaFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*5.38*/("""

"""),_display_(/*7.2*/Java(MovingWidgetName)),format.raw/*7.24*/(""" """),format.raw/*7.25*/("""movingElement = ("""),_display_(/*7.43*/Java(MovingWidgetName)),format.raw/*7.65*/(""") w.getModelElement();

try """),format.raw/*9.5*/("""{"""),format.raw/*9.6*/("""
	"""),format.raw/*10.2*/("""// Safety Check
	if (movingElement==null)"""),format.raw/*11.26*/("""{"""),format.raw/*11.27*/("""return;"""),format.raw/*11.34*/("""}"""),format.raw/*11.35*/("""

	"""),format.raw/*13.2*/("""// Get sourceWidget for card being dragged
	Widget sourceWidget=theGame.getContainer().getDragSource();

	// Safety Check
	if (sourceWidget==null)"""),format.raw/*17.25*/("""{"""),format.raw/*17.26*/("""return;"""),format.raw/*17.33*/("""}"""),format.raw/*17.34*/("""

	"""),_display_(/*19.3*/Java(TargetWidgetName)),format.raw/*19.25*/(""" """),format.raw/*19.26*/("""toElement=("""),_display_(/*19.38*/Java(TargetWidgetName)),format.raw/*19.60*/(""")src.getModelElement();

	// Identify the source
	"""),_display_(/*22.3*/Java(SourceWidgetName)),format.raw/*22.25*/(""" """),format.raw/*22.26*/("""sourceEntity = ("""),_display_(/*22.43*/Java(SourceWidgetName)),format.raw/*22.65*/(""")sourceWidget.getModelElement();

	// this is the actual move
	Move m = new """),_display_(/*25.16*/{Java(TheMove)}),format.raw/*25.31*/("""(sourceEntity,movingElement,toElement);

	if (m.valid(theGame))"""),format.raw/*27.23*/("""{"""),format.raw/*27.24*/("""
		"""),format.raw/*28.3*/("""m.doMove(theGame);
		theGame.pushMove(m);
	"""),format.raw/*30.2*/("""}"""),format.raw/*30.3*/(""" """),format.raw/*30.4*/("""else """),format.raw/*30.9*/("""{"""),format.raw/*30.10*/("""
		"""),format.raw/*31.3*/("""sourceWidget.returnWidget(w);
	"""),format.raw/*32.2*/("""}"""),format.raw/*32.3*/("""
"""),format.raw/*33.1*/("""}"""),format.raw/*33.2*/(""" """),format.raw/*33.3*/("""catch (ClassCastException cce) """),format.raw/*33.34*/("""{"""),format.raw/*33.35*/("""
	"""),format.raw/*34.2*/("""// silently ignore classCastException since that is a sign of
	// ordering issues with regards to multiple releases
"""),format.raw/*36.1*/("""}"""),format.raw/*36.2*/("""
"""))
      }
    }
  }

  def render(RootPackage:Name,TheMove:SimpleName,MovingWidgetName:SimpleName,SourceWidgetName:SimpleName,TargetWidgetName:SimpleName): org.combinators.templating.twirl.JavaFormat.Appendable = apply(RootPackage,TheMove,MovingWidgetName,SourceWidgetName,TargetWidgetName)

  def f:((Name,SimpleName,SimpleName,SimpleName,SimpleName) => org.combinators.templating.twirl.JavaFormat.Appendable) = (RootPackage,TheMove,MovingWidgetName,SourceWidgetName,TargetWidgetName) => apply(RootPackage,TheMove,MovingWidgetName,SourceWidgetName,TargetWidgetName)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: Tue Oct 04 13:18:10 EDT 2022
                  SOURCE: /home/ben/IdeaProjects/nextgen-solitaire/src/main/java-templates/org/combinators/solitaire/shared/controller/MoveWidgetToWidgetStatements.scala.java
                  HASH: 45a553e4c3a432a497a2f9d4861a0a1b21b40d2e
                  MATRIX: 833->1|1108->163|1136->166|1178->188|1206->189|1250->207|1292->229|1346->257|1373->258|1402->260|1471->301|1500->302|1535->309|1564->310|1594->313|1768->459|1797->460|1832->467|1861->468|1891->472|1934->494|1963->495|2002->507|2045->529|2122->580|2165->602|2194->603|2238->620|2281->642|2385->719|2421->734|2512->797|2541->798|2571->801|2641->844|2669->845|2697->846|2729->851|2758->852|2788->855|2846->886|2874->887|2902->888|2930->889|2958->890|3017->921|3046->922|3075->924|3218->1040|3246->1041
                  LINES: 16->1|25->5|27->7|27->7|27->7|27->7|27->7|29->9|29->9|30->10|31->11|31->11|31->11|31->11|33->13|37->17|37->17|37->17|37->17|39->19|39->19|39->19|39->19|39->19|42->22|42->22|42->22|42->22|42->22|45->25|45->25|47->27|47->27|48->28|50->30|50->30|50->30|50->30|50->30|51->31|52->32|52->32|53->33|53->33|53->33|53->33|53->33|54->34|56->36|56->36
                  -- GENERATED --
              */
          