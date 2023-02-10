
package org.combinators.solitaire.shared.moves.java

import org.combinators.templating.twirl.Java
import org.combinators.templating.twirl.Python
import com.github.javaparser.ast._
import com.github.javaparser.ast.body._
import com.github.javaparser.ast.comments._
import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import com.github.javaparser.ast.`type`._

object MultiMove extends _root_.play.twirl.api.BaseScalaTemplate[org.combinators.templating.twirl.JavaFormat.Appendable,_root_.play.twirl.api.Format[org.combinators.templating.twirl.JavaFormat.Appendable]](org.combinators.templating.twirl.JavaFormat) with _root_.play.twirl.api.Template6[Name,SimpleName,Seq[BodyDeclaration[_$1] forSome { 
   type _$1
}],Seq[Statement],Seq[Statement],Seq[Statement],org.combinators.templating.twirl.JavaFormat.Appendable] {

  /**/
  def apply/*1.2*/(RootPackage: Name,
        MoveName: SimpleName,
        Helper: Seq[BodyDeclaration[_]],
        Do: Seq[Statement],
        Undo: Seq[Statement],
        CheckValid: Seq[Statement]):org.combinators.templating.twirl.JavaFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*6.36*/("""
"""),format.raw/*7.1*/("""package """),_display_(/*7.10*/{Java(RootPackage)}),format.raw/*7.29*/(""".model;

import ks.common.model.*;
import ks.common.games.Solitaire;
import org.combinators.*;

/**
 * Move element from one stack to a number of other stacks.
 */
public class """),_display_(/*16.15*/Java(MoveName)),format.raw/*16.29*/(""" """),format.raw/*16.30*/("""extends ks.common.model.Move """),format.raw/*16.59*/("""{"""),format.raw/*16.60*/("""

    """),format.raw/*18.5*/("""/** Destination. */
    protected Stack[] destinations;

    /** Source. */
    protected Stack source;

    public """),_display_(/*24.13*/Java(MoveName)),format.raw/*24.27*/(""" """),format.raw/*24.28*/("""(Stack from, Stack[] to) """),format.raw/*24.53*/("""{"""),format.raw/*24.54*/("""
        """),format.raw/*25.9*/("""super();

        this.source = from;
        this.destinations = to;
    """),format.raw/*29.5*/("""}"""),format.raw/*29.6*/("""

    """),format.raw/*31.5*/("""// helper methods go here...
    // but also additional fields...
    // but also additional constructors...
    """),_display_(/*34.6*/Java(Helper)),format.raw/*34.18*/("""

    """),format.raw/*36.5*/("""/**
     * Request the undo of a move.
     *
     * @param theGame ks.games.Solitaire
     */
    public boolean undo(ks.common.games.Solitaire game) """),format.raw/*41.57*/("""{"""),format.raw/*41.58*/("""

        """),format.raw/*43.9*/("""// move back
        """),_display_(/*44.10*/Java(Undo)),format.raw/*44.20*/("""
    """),format.raw/*45.5*/("""}"""),format.raw/*45.6*/("""

    """),format.raw/*47.5*/("""/**
     * Execute the move.
     *
     * @see ks.common.model.Move#doMove(ks.games.Solitaire)
     */
    public boolean doMove(Solitaire game) """),format.raw/*52.43*/("""{"""),format.raw/*52.44*/("""
        """),format.raw/*53.9*/("""if (!valid (game)) """),format.raw/*53.28*/("""{"""),format.raw/*53.29*/("""
            """),format.raw/*54.13*/("""return false;
        """),format.raw/*55.9*/("""}"""),format.raw/*55.10*/("""

        """),_display_(/*57.10*/Java(Do)),format.raw/*57.18*/("""

        """),format.raw/*59.9*/("""return true;
    """),format.raw/*60.5*/("""}"""),format.raw/*60.6*/("""

    """),format.raw/*62.5*/("""/**
     * Validate the move.
     *
     * @see ks.common.model.Move#valid(ks.games.Solitaire)
     */
    public boolean valid(Solitaire game) """),format.raw/*67.42*/("""{"""),format.raw/*67.43*/("""

        """),_display_(/*69.10*/Java(CheckValid)),format.raw/*69.26*/("""
    """),format.raw/*70.5*/("""}"""),format.raw/*70.6*/("""
"""),format.raw/*71.1*/("""}"""),format.raw/*71.2*/("""
"""))
      }
    }
  }

  def render(RootPackage:Name,MoveName:SimpleName,Helper:Seq[BodyDeclaration[_$1] forSome { 
   type _$1
}],Do:Seq[Statement],Undo:Seq[Statement],CheckValid:Seq[Statement]): org.combinators.templating.twirl.JavaFormat.Appendable = apply(RootPackage,MoveName,Helper,Do,Undo,CheckValid)

  def f:((Name,SimpleName,Seq[BodyDeclaration[_$1] forSome { 
   type _$1
}],Seq[Statement],Seq[Statement],Seq[Statement]) => org.combinators.templating.twirl.JavaFormat.Appendable) = (RootPackage,MoveName,Helper,Do,Undo,CheckValid) => apply(RootPackage,MoveName,Helper,Do,Undo,CheckValid)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: Tue Oct 04 13:18:10 EDT 2022
                  SOURCE: /home/ben/IdeaProjects/nextgen-solitaire/src/main/java-templates/org/combinators/solitaire/shared/moves/MultiMove.scala.java
                  HASH: b73487d3e1403aece45b2577429c117d33837cf7
                  MATRIX: 872->1|1169->185|1196->186|1231->195|1270->214|1475->392|1510->406|1539->407|1596->436|1625->437|1658->443|1802->560|1837->574|1866->575|1919->600|1948->601|1984->610|2085->684|2113->685|2146->691|2286->805|2319->817|2352->823|2531->975|2560->976|2597->986|2646->1008|2677->1018|2709->1023|2737->1024|2770->1030|2944->1177|2973->1178|3009->1187|3056->1206|3085->1207|3126->1220|3175->1242|3204->1243|3242->1254|3271->1262|3308->1272|3352->1289|3380->1290|3413->1296|3586->1442|3615->1443|3653->1454|3690->1470|3722->1475|3750->1476|3778->1477|3806->1478
                  LINES: 18->1|28->6|29->7|29->7|29->7|38->16|38->16|38->16|38->16|38->16|40->18|46->24|46->24|46->24|46->24|46->24|47->25|51->29|51->29|53->31|56->34|56->34|58->36|63->41|63->41|65->43|66->44|66->44|67->45|67->45|69->47|74->52|74->52|75->53|75->53|75->53|76->54|77->55|77->55|79->57|79->57|81->59|82->60|82->60|84->62|89->67|89->67|91->69|91->69|92->70|92->70|93->71|93->71
                  -- GENERATED --
              */
          