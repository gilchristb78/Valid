
package org.combinators.solitaire.shared.java

import org.combinators.templating.twirl.Java
import org.combinators.templating.twirl.Python
import com.github.javaparser.ast._
import com.github.javaparser.ast.body._
import com.github.javaparser.ast.comments._
import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import com.github.javaparser.ast.`type`._

object GameTemplate extends _root_.play.twirl.api.BaseScalaTemplate[org.combinators.templating.twirl.JavaFormat.Appendable,_root_.play.twirl.api.Format[org.combinators.templating.twirl.JavaFormat.Appendable]](org.combinators.templating.twirl.JavaFormat) with _root_.play.twirl.api.Template4[Name,SimpleName,Seq[Statement],Seq[Statement],org.combinators.templating.twirl.JavaFormat.Appendable] {

  /**/
  def apply/*1.2*/(rootPackage:Name,
    nameParameter:SimpleName,
    winParameter:Seq[Statement],
    initializeSteps:Seq[Statement]):org.combinators.templating.twirl.JavaFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*4.36*/("""

"""),format.raw/*6.1*/("""package """),_display_(/*6.10*/Java(rootPackage)),format.raw/*6.27*/(""";

// these are still too many to include all at once.

import ks.common.view.*;
import ks.common.controller.*;
import ks.common.model.*;
import ks.common.games.*;
import ks.client.gamefactory.GameWindow;
import ks.launcher.Main;

import java.awt.event.*;
import java.awt.Dimension;

/**
 * The Game plugin is the constant upon which all other plugins are refined.
 * __p__
 * It is a fully working plugin that has absolutely no behavior or meaning. It won't have the
 * Score or NumberOfCardsLeft, but it will at least be able to show a blank playing field.
 * __p__
 * __author: George T. Heineman (heineman__cs.wpi.edu)
 */
public class """),_display_(/*28.15*/Java(nameParameter)),format.raw/*28.34*/(""" """),format.raw/*28.35*/("""extends Solitaire """),format.raw/*28.53*/("""{"""),format.raw/*28.54*/("""

    """),format.raw/*30.5*/("""/** Enable refinements to determine whether game has been won. */
    public boolean hasWon() """),format.raw/*31.29*/("""{"""),format.raw/*31.30*/("""
        """),_display_(/*32.10*/Java(winParameter)),format.raw/*32.28*/("""
        """),format.raw/*33.9*/("""return false;
    """),format.raw/*34.5*/("""}"""),format.raw/*34.6*/("""

    """),format.raw/*36.5*/("""/**
     * Refinement determines initializations.
     */
    public void initialize() """),format.raw/*39.30*/("""{"""),format.raw/*39.31*/("""
        """),_display_(/*40.10*/Java(initializeSteps)),format.raw/*40.31*/("""

        """),format.raw/*42.9*/("""// Cover the Container for any events not handled by a widget:
        getContainer().setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
        getContainer().setMouseAdapter(new SolitaireReleasedAdapter(this));
        getContainer().setUndoAdapter(new SolitaireUndoAdapter(this));
    """),format.raw/*46.5*/("""}"""),format.raw/*46.6*/("""

    """),format.raw/*48.5*/("""/**
     * Refinement determines name.
     */
    public String getName() """),format.raw/*51.29*/("""{"""),format.raw/*51.30*/("""
        """),format.raw/*52.9*/("""return """"),_display_(/*52.18*/nameParameter),format.raw/*52.31*/("""";   // special case to be handled in parser specially. Parser quotes.
    """),format.raw/*53.5*/("""}"""),format.raw/*53.6*/("""

    """),format.raw/*55.5*/("""/**
     * Helper routine for setting default widgets. This is defined so that any future layer
     * can use this method to define a reasonable default set of controllers for the widget.
     */
    protected void setDefaultControllers(Widget w) """),format.raw/*59.52*/("""{"""),format.raw/*59.53*/("""
        """),format.raw/*60.9*/("""w.setMouseMotionAdapter(new ks.common.controller.SolitaireMouseMotionAdapter(this));
        w.setMouseAdapter(new ks.common.controller.SolitaireReleasedAdapter(this));
        w.setUndoAdapter(new SolitaireUndoAdapter(this));
    """),format.raw/*63.5*/("""}"""),format.raw/*63.6*/("""

    """),format.raw/*65.5*/("""// force to be able to launch directly.
    public static void main(String[] args) """),format.raw/*66.44*/("""{"""),format.raw/*66.45*/("""
        """),format.raw/*67.9*/("""final GameWindow gw = Main.generateWindow(new """),_display_(/*67.56*/Java(nameParameter)),format.raw/*67.75*/(""" """),format.raw/*67.76*/("""(), Deck.OrderBySuit);
        // properly exist program once selected.
        gw.addWindowListener(new WindowAdapter() """),format.raw/*69.50*/("""{"""),format.raw/*69.51*/("""
            """),format.raw/*70.13*/("""public void windowClosing(WindowEvent we) """),format.raw/*70.55*/("""{"""),format.raw/*70.56*/("""
                """),format.raw/*71.17*/("""System.exit(0);
            """),format.raw/*72.13*/("""}"""),format.raw/*72.14*/("""
        """),format.raw/*73.9*/("""}"""),format.raw/*73.10*/(""");
        gw.setVisible(true);

    """),format.raw/*76.5*/("""}"""),format.raw/*76.6*/("""
"""),format.raw/*77.1*/("""}"""),format.raw/*77.2*/("""
"""))
      }
    }
  }

  def render(rootPackage:Name,nameParameter:SimpleName,winParameter:Seq[Statement],initializeSteps:Seq[Statement]): org.combinators.templating.twirl.JavaFormat.Appendable = apply(rootPackage,nameParameter,winParameter,initializeSteps)

  def f:((Name,SimpleName,Seq[Statement],Seq[Statement]) => org.combinators.templating.twirl.JavaFormat.Appendable) = (rootPackage,nameParameter,winParameter,initializeSteps) => apply(rootPackage,nameParameter,winParameter,initializeSteps)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: Tue Oct 04 13:18:10 EDT 2022
                  SOURCE: /home/ben/IdeaProjects/nextgen-solitaire/src/main/java-templates/org/combinators/solitaire/shared/GameTemplate.scala.java
                  HASH: b1122e022ccca93514a61aa6d561ed87b489a323
                  MATRIX: 803->1|1033->118|1061->120|1096->129|1133->146|1801->787|1841->806|1870->807|1916->825|1945->826|1978->832|2100->926|2129->927|2166->937|2205->955|2241->964|2286->982|2314->983|2347->989|2462->1076|2491->1077|2528->1087|2570->1108|2607->1118|2933->1417|2961->1418|2994->1424|3097->1499|3126->1500|3162->1509|3198->1518|3232->1531|3334->1606|3362->1607|3395->1613|3671->1861|3700->1862|3736->1871|3994->2102|4022->2103|4055->2109|4166->2192|4195->2193|4231->2202|4305->2249|4345->2268|4374->2269|4523->2390|4552->2391|4593->2404|4663->2446|4692->2447|4737->2464|4793->2492|4822->2493|4858->2502|4887->2503|4951->2540|4979->2541|5007->2542|5035->2543
                  LINES: 16->1|24->4|26->6|26->6|26->6|48->28|48->28|48->28|48->28|48->28|50->30|51->31|51->31|52->32|52->32|53->33|54->34|54->34|56->36|59->39|59->39|60->40|60->40|62->42|66->46|66->46|68->48|71->51|71->51|72->52|72->52|72->52|73->53|73->53|75->55|79->59|79->59|80->60|83->63|83->63|85->65|86->66|86->66|87->67|87->67|87->67|87->67|89->69|89->69|90->70|90->70|90->70|91->71|92->72|92->72|93->73|93->73|96->76|96->76|97->77|97->77
                  -- GENERATED --
              */
          