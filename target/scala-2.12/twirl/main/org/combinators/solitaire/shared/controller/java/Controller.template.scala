
package org.combinators.solitaire.shared.controller.java

import org.combinators.templating.twirl.Java
import org.combinators.templating.twirl.Python
import com.github.javaparser.ast._
import com.github.javaparser.ast.body._
import com.github.javaparser.ast.comments._
import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import com.github.javaparser.ast.`type`._

object Controller extends _root_.play.twirl.api.BaseScalaTemplate[org.combinators.templating.twirl.JavaFormat.Appendable,_root_.play.twirl.api.Format[org.combinators.templating.twirl.JavaFormat.Appendable]](org.combinators.templating.twirl.JavaFormat) with _root_.play.twirl.api.Template7[Name,SimpleName,SimpleName,Seq[Statement],Seq[Statement],_root_.scala.Function2[SimpleName, SimpleName, Seq[Statement]],Seq[Statement],org.combinators.templating.twirl.JavaFormat.Appendable] {

  /**/
  def apply/*1.2*/(RootPackage: Name,
   Designate: SimpleName,
   NameOfTheGame : SimpleName,
   AutoMoves : Seq[Statement],
   MouseClicked: Seq[Statement],
   MousePressed: (SimpleName, SimpleName) => Seq[Statement],
   MouseReleased: Seq[Statement]):org.combinators.templating.twirl.JavaFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*7.34*/("""
"""),format.raw/*8.1*/("""package """),_display_(/*8.10*/{Java(RootPackage)}),format.raw/*8.29*/(""".controller;

import """),_display_(/*10.9*/{Java(RootPackage)}),format.raw/*10.28*/(""".*;
import """),_display_(/*11.9*/{Java(RootPackage)}),format.raw/*11.28*/(""".model.*;    // where move classes are placed.
import java.awt.event.MouseEvent;
import ks.common.model.*;
import ks.common.view.*;
import ks.common.games.*;
import ks.common.controller.*;

public class """),_display_(/*18.15*/{Java(Designate)}),format.raw/*18.32*/("""Controller extends SolitaireReleasedAdapter """),format.raw/*18.76*/("""{"""),format.raw/*18.77*/("""
	"""),format.raw/*19.2*/("""protected """),_display_(/*19.13*/Java(NameOfTheGame)),format.raw/*19.32*/(""" """),format.raw/*19.33*/("""theGame;

	/** The View being controlled */
	protected """),_display_(/*22.13*/{Java(Designate)}),format.raw/*22.30*/("""View src;

	public """),_display_(/*24.10*/{Java(Designate)}),format.raw/*24.27*/("""Controller("""),_display_(/*24.39*/Java(NameOfTheGame)),format.raw/*24.58*/(""" """),format.raw/*24.59*/("""theGame, """),_display_(/*24.69*/{Java(Designate)}),format.raw/*24.86*/("""View src) """),format.raw/*24.96*/("""{"""),format.raw/*24.97*/("""
		"""),format.raw/*25.3*/("""super(theGame);

		this.theGame = theGame;
		this.src = src;
	"""),format.raw/*29.2*/("""}"""),format.raw/*29.3*/("""

	"""),format.raw/*31.2*/("""public void mouseClicked(MouseEvent me) """),format.raw/*31.42*/("""{"""),format.raw/*31.43*/("""
		"""),_display_(/*32.4*/Java(MouseClicked)),format.raw/*32.22*/("""
	"""),format.raw/*33.2*/("""}"""),format.raw/*33.3*/("""

	"""),format.raw/*35.2*/("""public void mousePressed(MouseEvent me) """),format.raw/*35.42*/("""{"""),format.raw/*35.43*/("""
	    """),format.raw/*36.6*/("""Container c = theGame.getContainer();

		// Another Safety Check
		Widget w = c.getActiveDraggingObject();
		if (w != Container.getNothingBeingDragged()) """),format.raw/*40.48*/("""{"""),format.raw/*40.49*/("""
			"""),format.raw/*41.4*/("""System.err.println("mousePressed: Unexpectedly encountered a Dragging Object during a Mouse press.");
			return;
		"""),format.raw/*43.3*/("""}"""),format.raw/*43.4*/("""

		"""),format.raw/*45.3*/("""// should we ignore this
		boolean me_ignore = true;
		Widget me_widget = null;

		// must both define me_ignore to false and set me_widget to valid widget
		"""),_display_(/*50.4*/Java(MousePressed(Java("me_widget").simpleName(), Java("me_ignore").simpleName()))),format.raw/*50.86*/("""

		"""),format.raw/*52.3*/("""if (me_ignore) """),format.raw/*52.18*/("""{"""),format.raw/*52.19*/("""
			"""),format.raw/*53.4*/("""return;
		"""),format.raw/*54.3*/("""}"""),format.raw/*54.4*/("""

		"""),format.raw/*56.3*/("""// We tell the container what item is being dragged (and where in the Widget it was clicked)...
		c.setActiveDraggingObject(me_widget, me);

		// and where it came from
		c.setDragSource(src);

		c.repaint();
	"""),format.raw/*63.2*/("""}"""),format.raw/*63.3*/("""

	"""),format.raw/*65.2*/("""public void mouseReleased(MouseEvent me) """),format.raw/*65.43*/("""{"""),format.raw/*65.44*/("""
		"""),format.raw/*66.3*/("""Container c = theGame.getContainer();

		// Safety Check
		Widget w = c.getActiveDraggingObject();
		if (w == Container.getNothingBeingDragged()) """),format.raw/*70.48*/("""{"""),format.raw/*70.49*/("""
			"""),format.raw/*71.4*/("""return;
		"""),format.raw/*72.3*/("""}"""),format.raw/*72.4*/("""

		"""),_display_(/*74.4*/Java(MouseReleased)),format.raw/*74.23*/("""

		"""),_display_(/*76.4*/Java(AutoMoves)),format.raw/*76.19*/("""

		"""),format.raw/*78.3*/("""// release the dragging object and refresh display
		c.releaseDraggingObject();
		c.repaint();
	"""),format.raw/*81.2*/("""}"""),format.raw/*81.3*/("""
"""),format.raw/*82.1*/("""}"""),format.raw/*82.2*/("""
"""))
      }
    }
  }

  def render(RootPackage:Name,Designate:SimpleName,NameOfTheGame:SimpleName,AutoMoves:Seq[Statement],MouseClicked:Seq[Statement],MousePressed:_root_.scala.Function2[SimpleName, SimpleName, Seq[Statement]],MouseReleased:Seq[Statement]): org.combinators.templating.twirl.JavaFormat.Appendable = apply(RootPackage,Designate,NameOfTheGame,AutoMoves,MouseClicked,MousePressed,MouseReleased)

  def f:((Name,SimpleName,SimpleName,Seq[Statement],Seq[Statement],_root_.scala.Function2[SimpleName, SimpleName, Seq[Statement]],Seq[Statement]) => org.combinators.templating.twirl.JavaFormat.Appendable) = (RootPackage,Designate,NameOfTheGame,AutoMoves,MouseClicked,MousePressed,MouseReleased) => apply(RootPackage,Designate,NameOfTheGame,AutoMoves,MouseClicked,MousePressed,MouseReleased)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: Tue Oct 04 13:18:10 EDT 2022
                  SOURCE: /home/ben/IdeaProjects/nextgen-solitaire/src/main/java-templates/org/combinators/solitaire/shared/controller/Controller.scala.java
                  HASH: 29df9fa4534cdad0abf6e9e14b609652c923c6ee
                  MATRIX: 901->1|1249->236|1276->237|1311->246|1350->265|1398->287|1438->306|1476->318|1516->337|1747->541|1785->558|1857->602|1886->603|1915->605|1953->616|1993->635|2022->636|2105->692|2143->709|2190->729|2228->746|2267->758|2307->777|2336->778|2373->788|2411->805|2449->815|2478->816|2508->819|2597->881|2625->882|2655->885|2723->925|2752->926|2782->930|2821->948|2850->950|2878->951|2908->954|2976->994|3005->995|3038->1001|3220->1155|3249->1156|3280->1160|3422->1275|3450->1276|3481->1280|3666->1439|3769->1521|3800->1525|3843->1540|3872->1541|3903->1545|3940->1555|3968->1556|3999->1560|4236->1770|4264->1771|4294->1774|4363->1815|4392->1816|4422->1819|4596->1965|4625->1966|4656->1970|4693->1980|4721->1981|4752->1986|4792->2005|4823->2010|4859->2025|4890->2029|5013->2125|5041->2126|5069->2127|5097->2128
                  LINES: 16->1|27->7|28->8|28->8|28->8|30->10|30->10|31->11|31->11|38->18|38->18|38->18|38->18|39->19|39->19|39->19|39->19|42->22|42->22|44->24|44->24|44->24|44->24|44->24|44->24|44->24|44->24|44->24|45->25|49->29|49->29|51->31|51->31|51->31|52->32|52->32|53->33|53->33|55->35|55->35|55->35|56->36|60->40|60->40|61->41|63->43|63->43|65->45|70->50|70->50|72->52|72->52|72->52|73->53|74->54|74->54|76->56|83->63|83->63|85->65|85->65|85->65|86->66|90->70|90->70|91->71|92->72|92->72|94->74|94->74|96->76|96->76|98->78|101->81|101->81|102->82|102->82
                  -- GENERATED --
              */
          