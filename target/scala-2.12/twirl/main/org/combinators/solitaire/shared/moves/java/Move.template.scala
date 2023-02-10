
package org.combinators.solitaire.shared.moves.java

import org.combinators.templating.twirl.Java
import org.combinators.templating.twirl.Python
import com.github.javaparser.ast._
import com.github.javaparser.ast.body._
import com.github.javaparser.ast.comments._
import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import com.github.javaparser.ast.`type`._

object Move extends _root_.play.twirl.api.BaseScalaTemplate[org.combinators.templating.twirl.JavaFormat.Appendable,_root_.play.twirl.api.Format[org.combinators.templating.twirl.JavaFormat.Appendable]](org.combinators.templating.twirl.JavaFormat) with _root_.play.twirl.api.Template6[Name,SimpleName,Seq[BodyDeclaration[_$1] forSome { 
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
 * Move element from one stack to another.
 */
public class """),_display_(/*16.15*/Java(MoveName)),format.raw/*16.29*/(""" """),format.raw/*16.30*/("""extends Move """),format.raw/*16.43*/("""{"""),format.raw/*16.44*/("""
    """),format.raw/*17.5*/("""protected Stack destination;
    protected Stack source;

    public """),_display_(/*20.13*/Java(MoveName)),format.raw/*20.27*/(""" """),format.raw/*20.28*/("""(Stack from, Stack to) """),format.raw/*20.51*/("""{"""),format.raw/*20.52*/("""
        """),format.raw/*21.9*/("""super();

        this.source = from;
        this.destination = to;
    """),format.raw/*25.5*/("""}"""),format.raw/*25.6*/("""

    """),format.raw/*27.5*/("""// Extra fields, methods and constructors brought in here
    """),_display_(/*28.6*/Java(Helper)),format.raw/*28.18*/("""

    """),format.raw/*30.5*/("""/**
     * Request the undo of a move.
     *
     * @param theGame ks.games.Solitaire
     */
    public boolean undo(Solitaire game) """),format.raw/*35.41*/("""{"""),format.raw/*35.42*/("""
        """),_display_(/*36.10*/Java(Undo)),format.raw/*36.20*/("""
    """),format.raw/*37.5*/("""}"""),format.raw/*37.6*/("""

    """),format.raw/*39.5*/("""/**
     * Execute the move.
     *
     * @see ks.common.model.Move#doMove(ks.games.Solitaire)
     */
    public boolean doMove(Solitaire game) """),format.raw/*44.43*/("""{"""),format.raw/*44.44*/("""
        """),format.raw/*45.9*/("""if (!valid (game)) """),format.raw/*45.28*/("""{"""),format.raw/*45.29*/(""" """),format.raw/*45.30*/("""return false; """),format.raw/*45.44*/("""}"""),format.raw/*45.45*/("""

        """),_display_(/*47.10*/Java(Do)),format.raw/*47.18*/("""
        """),format.raw/*48.9*/("""return true;
    """),format.raw/*49.5*/("""}"""),format.raw/*49.6*/("""

    """),format.raw/*51.5*/("""/**
     * Validate the move.
     *
     * @see ks.common.model.Move#valid(ks.games.Solitaire)
     */
    public boolean valid(Solitaire game) """),format.raw/*56.42*/("""{"""),format.raw/*56.43*/("""
        """),_display_(/*57.10*/Java(CheckValid)),format.raw/*57.26*/("""
    """),format.raw/*58.5*/("""}"""),format.raw/*58.6*/("""
"""),format.raw/*59.1*/("""}"""),format.raw/*59.2*/("""
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
                  SOURCE: /home/ben/IdeaProjects/nextgen-solitaire/src/main/java-templates/org/combinators/solitaire/shared/moves/Move.scala.java
                  HASH: c1f2c2367cbdaa4882c535fa4a5e3ee28292b768
                  MATRIX: 867->1|1164->185|1191->186|1226->195|1265->214|1453->375|1488->389|1517->390|1558->403|1587->404|1619->409|1716->479|1751->493|1780->494|1831->517|1860->518|1896->527|1996->600|2024->601|2057->607|2146->670|2179->682|2212->688|2375->824|2404->825|2441->835|2472->845|2504->850|2532->851|2565->857|2739->1004|2768->1005|2804->1014|2851->1033|2880->1034|2909->1035|2951->1049|2980->1050|3018->1061|3047->1069|3083->1078|3127->1095|3155->1096|3188->1102|3361->1248|3390->1249|3427->1259|3464->1275|3496->1280|3524->1281|3552->1282|3580->1283
                  LINES: 18->1|28->6|29->7|29->7|29->7|38->16|38->16|38->16|38->16|38->16|39->17|42->20|42->20|42->20|42->20|42->20|43->21|47->25|47->25|49->27|50->28|50->28|52->30|57->35|57->35|58->36|58->36|59->37|59->37|61->39|66->44|66->44|67->45|67->45|67->45|67->45|67->45|67->45|69->47|69->47|70->48|71->49|71->49|73->51|78->56|78->56|79->57|79->57|80->58|80->58|81->59|81->59
                  -- GENERATED --
              */
          