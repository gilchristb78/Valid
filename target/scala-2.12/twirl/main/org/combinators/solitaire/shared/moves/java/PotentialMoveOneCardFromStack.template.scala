
package org.combinators.solitaire.shared.moves.java

import org.combinators.templating.twirl.Java
import org.combinators.templating.twirl.Python
import com.github.javaparser.ast._
import com.github.javaparser.ast.body._
import com.github.javaparser.ast.comments._
import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import com.github.javaparser.ast.`type`._

object PotentialMoveOneCardFromStack extends _root_.play.twirl.api.BaseScalaTemplate[org.combinators.templating.twirl.JavaFormat.Appendable,_root_.play.twirl.api.Format[org.combinators.templating.twirl.JavaFormat.Appendable]](org.combinators.templating.twirl.JavaFormat) with _root_.play.twirl.api.Template4[Name,SimpleName,SimpleName,SimpleName,org.combinators.templating.twirl.JavaFormat.Appendable] {

  /**/
  def apply/*1.2*/(RootPackage: Name, MoveName: SimpleName, DraggingCardVariableName: SimpleName, Type: SimpleName):org.combinators.templating.twirl.JavaFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.99*/("""
"""),format.raw/*2.1*/("""package """),_display_(/*2.10*/{Java(RootPackage)}),format.raw/*2.29*/(""".model;

import ks.common.model.*;
import ks.common.games.Solitaire;

/**
 * Potential Move when multiple cards in play; note that 'numCards' is inherited, and is
 * drawn from the MoveHelper combinators that created the parent Move classes in the first place.
 *
 * Parameters:
 *    RootPackage
 *    Designate
 *    DraggingCard
 */
public class Potential"""),_display_(/*16.24*/{Java(MoveName)}),format.raw/*16.40*/(""" """),format.raw/*16.41*/("""extends """),_display_(/*16.50*/{Java(MoveName)}),format.raw/*16.66*/(""" """),format.raw/*16.67*/("""{"""),format.raw/*16.68*/("""

    """),format.raw/*18.5*/("""/** Destination. */
    public Potential"""),_display_(/*19.22*/{Java(MoveName)}),format.raw/*19.38*/(""" """),format.raw/*19.39*/("""(Stack from, Stack to) """),format.raw/*19.62*/("""{"""),format.raw/*19.63*/("""
        """),format.raw/*20.9*/("""super(from, to);
        numInColumn = 1;
    """),format.raw/*22.5*/("""}"""),format.raw/*22.6*/("""

    """),format.raw/*24.5*/("""public Potential"""),_display_(/*24.22*/{Java(MoveName)}),format.raw/*24.38*/(""" """),format.raw/*24.39*/("""(Stack from, Stack to, int num) """),format.raw/*24.71*/("""{"""),format.raw/*24.72*/("""
        """),format.raw/*25.9*/("""super(from, to);
        numInColumn = num;
    """),format.raw/*27.5*/("""}"""),format.raw/*27.6*/("""

    """),format.raw/*29.5*/("""@Override
    public boolean valid(Solitaire game) """),format.raw/*30.42*/("""{"""),format.raw/*30.43*/("""
        """),format.raw/*31.9*/("""if ("""),_display_(/*31.14*/Java(DraggingCardVariableName)),format.raw/*31.44*/(""" """),format.raw/*31.45*/("""== null) """),format.raw/*31.54*/("""{"""),format.raw/*31.55*/("""
            """),format.raw/*32.13*/("""if (source.count() < numInColumn) """),format.raw/*32.47*/("""{"""),format.raw/*32.48*/(""" """),format.raw/*32.49*/("""return false; """),format.raw/*32.63*/("""}"""),format.raw/*32.64*/("""

            """),format.raw/*34.13*/("""// make sure to keep order of potential column intact
            synchronized (this) """),format.raw/*35.33*/("""{"""),format.raw/*35.34*/("""
                """),_display_(/*36.18*/Java(DraggingCardVariableName)),format.raw/*36.48*/(""" """),format.raw/*36.49*/("""= new """),_display_(/*36.56*/{Java(Type)}),format.raw/*36.68*/("""();
                source.select(numInColumn);
                """),_display_(/*38.18*/{Java(DraggingCardVariableName)}),format.raw/*38.50*/(""".push(source.getSelected());
                boolean result = super.valid(game);
                source.push( """),_display_(/*40.31*/Java(DraggingCardVariableName)),format.raw/*40.61*/(""" """),format.raw/*40.62*/(""");

                return result;
            """),format.raw/*43.13*/("""}"""),format.raw/*43.14*/("""
        """),format.raw/*44.9*/("""}"""),format.raw/*44.10*/(""" """),format.raw/*44.11*/("""else """),format.raw/*44.16*/("""{"""),format.raw/*44.17*/("""
            """),format.raw/*45.13*/("""return super.valid(game);
        """),format.raw/*46.9*/("""}"""),format.raw/*46.10*/("""
    """),format.raw/*47.5*/("""}"""),format.raw/*47.6*/("""

    """),format.raw/*49.5*/("""@Override
    public boolean doMove(Solitaire game) """),format.raw/*50.43*/("""{"""),format.raw/*50.44*/("""
        """),format.raw/*51.9*/("""if (!valid(game)) """),format.raw/*51.27*/("""{"""),format.raw/*51.28*/(""" """),format.raw/*51.29*/("""return false; """),format.raw/*51.43*/("""}"""),format.raw/*51.44*/("""

        """),format.raw/*53.9*/("""synchronized (this) """),format.raw/*53.29*/("""{"""),format.raw/*53.30*/("""
            """),_display_(/*54.14*/Java(DraggingCardVariableName)),format.raw/*54.44*/(""" """),format.raw/*54.45*/("""= new """),_display_(/*54.52*/{Java(Type)}),format.raw/*54.64*/("""();
            source.select(numInColumn);
            """),_display_(/*56.14*/{Java(DraggingCardVariableName)}),format.raw/*56.46*/(""".push(source.getSelected());
            boolean result = super.doMove(game);

            return result;
        """),format.raw/*60.9*/("""}"""),format.raw/*60.10*/("""
    """),format.raw/*61.5*/("""}"""),format.raw/*61.6*/("""
"""),format.raw/*62.1*/("""}"""))
      }
    }
  }

  def render(RootPackage:Name,MoveName:SimpleName,DraggingCardVariableName:SimpleName,Type:SimpleName): org.combinators.templating.twirl.JavaFormat.Appendable = apply(RootPackage,MoveName,DraggingCardVariableName,Type)

  def f:((Name,SimpleName,SimpleName,SimpleName) => org.combinators.templating.twirl.JavaFormat.Appendable) = (RootPackage,MoveName,DraggingCardVariableName,Type) => apply(RootPackage,MoveName,DraggingCardVariableName,Type)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: Tue Oct 04 13:18:10 EDT 2022
                  SOURCE: /home/ben/IdeaProjects/nextgen-solitaire/src/main/java-templates/org/combinators/solitaire/shared/moves/PotentialMoveOneCardFromStack.scala.java
                  HASH: f0978ecc1d1166d8d411e3ea337ae664f7eb691f
                  MATRIX: 818->1|1028->98|1055->99|1090->108|1129->127|1515->486|1552->502|1581->503|1617->512|1654->528|1683->529|1712->530|1745->536|1813->577|1850->593|1879->594|1930->617|1959->618|1995->627|2068->673|2096->674|2129->680|2173->697|2210->713|2239->714|2299->746|2328->747|2364->756|2439->804|2467->805|2500->811|2579->863|2608->864|2644->873|2676->878|2727->908|2756->909|2793->918|2822->919|2863->932|2925->966|2954->967|2983->968|3025->982|3054->983|3096->997|3210->1083|3239->1084|3284->1102|3335->1132|3364->1133|3398->1140|3431->1152|3523->1217|3576->1249|3714->1360|3765->1390|3794->1391|3869->1438|3898->1439|3934->1448|3963->1449|3992->1450|4025->1455|4054->1456|4095->1469|4156->1503|4185->1504|4217->1509|4245->1510|4278->1516|4358->1569|4387->1570|4423->1579|4469->1597|4498->1598|4527->1599|4569->1613|4598->1614|4635->1624|4683->1644|4712->1645|4753->1659|4804->1689|4833->1690|4867->1697|4900->1709|4984->1766|5037->1798|5178->1912|5207->1913|5239->1918|5267->1919|5295->1920
                  LINES: 16->1|21->1|22->2|22->2|22->2|36->16|36->16|36->16|36->16|36->16|36->16|36->16|38->18|39->19|39->19|39->19|39->19|39->19|40->20|42->22|42->22|44->24|44->24|44->24|44->24|44->24|44->24|45->25|47->27|47->27|49->29|50->30|50->30|51->31|51->31|51->31|51->31|51->31|51->31|52->32|52->32|52->32|52->32|52->32|52->32|54->34|55->35|55->35|56->36|56->36|56->36|56->36|56->36|58->38|58->38|60->40|60->40|60->40|63->43|63->43|64->44|64->44|64->44|64->44|64->44|65->45|66->46|66->46|67->47|67->47|69->49|70->50|70->50|71->51|71->51|71->51|71->51|71->51|71->51|73->53|73->53|73->53|74->54|74->54|74->54|74->54|74->54|76->56|76->56|80->60|80->60|81->61|81->61|82->62
                  -- GENERATED --
              */
          