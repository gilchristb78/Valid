
package org.combinators.solitaire.shared.moves.java

import org.combinators.templating.twirl.Java
import org.combinators.templating.twirl.Python
import com.github.javaparser.ast._
import com.github.javaparser.ast.body._
import com.github.javaparser.ast.comments._
import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import com.github.javaparser.ast.`type`._

object PotentialMove extends _root_.play.twirl.api.BaseScalaTemplate[org.combinators.templating.twirl.JavaFormat.Appendable,_root_.play.twirl.api.Format[org.combinators.templating.twirl.JavaFormat.Appendable]](org.combinators.templating.twirl.JavaFormat) with _root_.play.twirl.api.Template3[Name,SimpleName,SimpleName,org.combinators.templating.twirl.JavaFormat.Appendable] {

  /**/
  def apply/*1.2*/(RootPackage: Name, MoveName: SimpleName, DraggingCardVariableName: SimpleName):org.combinators.templating.twirl.JavaFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.81*/("""
"""),format.raw/*2.1*/("""package """),_display_(/*2.10*/{Java(RootPackage)}),format.raw/*2.29*/(""".model;

import ks.common.model.*;
import ks.common.games.Solitaire;

/**
 * Move element from one stack to another.
 *
 * Parameters:
 *    RootPackage
 *    Designate
 *    DraggingCard
 */
public class Potential"""),_display_(/*15.24*/{Java(MoveName)}),format.raw/*15.40*/(""" """),format.raw/*15.41*/("""extends """),_display_(/*15.50*/{Java(MoveName)}),format.raw/*15.66*/(""" """),format.raw/*15.67*/("""{"""),format.raw/*15.68*/("""

    """),format.raw/*17.5*/("""/** Destination. */
    public Potential"""),_display_(/*18.22*/{Java(MoveName)}),format.raw/*18.38*/(""" """),format.raw/*18.39*/("""(Stack from, Stack to) """),format.raw/*18.62*/("""{"""),format.raw/*18.63*/("""
        """),format.raw/*19.9*/("""super(from, to);
    """),format.raw/*20.5*/("""}"""),format.raw/*20.6*/("""

    """),format.raw/*22.5*/("""@Override
    public boolean valid(Solitaire game) """),format.raw/*23.42*/("""{"""),format.raw/*23.43*/("""
        """),format.raw/*24.9*/("""if ("""),_display_(/*24.14*/Java(DraggingCardVariableName)),format.raw/*24.44*/(""" """),format.raw/*24.45*/("""== null) """),format.raw/*24.54*/("""{"""),format.raw/*24.55*/("""
            """),format.raw/*25.13*/("""if (source.empty()) """),format.raw/*25.33*/("""{"""),format.raw/*25.34*/(""" """),format.raw/*25.35*/("""return false; """),format.raw/*25.49*/("""}"""),format.raw/*25.50*/("""

            """),format.raw/*27.13*/("""synchronized (this) """),format.raw/*27.33*/("""{"""),format.raw/*27.34*/("""
                """),_display_(/*28.18*/Java(DraggingCardVariableName)),format.raw/*28.48*/(""" """),format.raw/*28.49*/("""= source.get();
                boolean result = super.valid(game);
                source.add( """),_display_(/*30.30*/Java(DraggingCardVariableName)),format.raw/*30.60*/(""" """),format.raw/*30.61*/(""");

                return result;
            """),format.raw/*33.13*/("""}"""),format.raw/*33.14*/("""
        """),format.raw/*34.9*/("""}"""),format.raw/*34.10*/(""" """),format.raw/*34.11*/("""else """),format.raw/*34.16*/("""{"""),format.raw/*34.17*/("""
            """),format.raw/*35.13*/("""return super.valid(game);
        """),format.raw/*36.9*/("""}"""),format.raw/*36.10*/("""
    """),format.raw/*37.5*/("""}"""),format.raw/*37.6*/("""

    """),format.raw/*39.5*/("""@Override
    public boolean doMove(Solitaire game) """),format.raw/*40.43*/("""{"""),format.raw/*40.44*/("""
        """),format.raw/*41.9*/("""if (!valid(game)) """),format.raw/*41.27*/("""{"""),format.raw/*41.28*/(""" """),format.raw/*41.29*/("""return false; """),format.raw/*41.43*/("""}"""),format.raw/*41.44*/("""

        """),format.raw/*43.9*/("""synchronized (this) """),format.raw/*43.29*/("""{"""),format.raw/*43.30*/("""
            """),_display_(/*44.14*/Java(DraggingCardVariableName)),format.raw/*44.44*/(""" """),format.raw/*44.45*/("""= source.get();
            boolean result = super.doMove(game);

            return result;
        """),format.raw/*48.9*/("""}"""),format.raw/*48.10*/("""
    """),format.raw/*49.5*/("""}"""),format.raw/*49.6*/("""
"""),format.raw/*50.1*/("""}"""))
      }
    }
  }

  def render(RootPackage:Name,MoveName:SimpleName,DraggingCardVariableName:SimpleName): org.combinators.templating.twirl.JavaFormat.Appendable = apply(RootPackage,MoveName,DraggingCardVariableName)

  def f:((Name,SimpleName,SimpleName) => org.combinators.templating.twirl.JavaFormat.Appendable) = (RootPackage,MoveName,DraggingCardVariableName) => apply(RootPackage,MoveName,DraggingCardVariableName)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: Tue Oct 04 13:18:10 EDT 2022
                  SOURCE: /home/ben/IdeaProjects/nextgen-solitaire/src/main/java-templates/org/combinators/solitaire/shared/moves/PotentialMove.scala.java
                  HASH: fd9eac1d2be13cc66bfca1a692673dc4bf7dd30d
                  MATRIX: 791->1|983->80|1010->81|1045->90|1084->109|1326->324|1363->340|1392->341|1428->350|1465->366|1494->367|1523->368|1556->374|1624->415|1661->431|1690->432|1741->455|1770->456|1806->465|1854->486|1882->487|1915->493|1994->545|2023->546|2059->555|2091->560|2142->590|2171->591|2208->600|2237->601|2278->614|2326->634|2355->635|2384->636|2426->650|2455->651|2497->665|2545->685|2574->686|2619->704|2670->734|2699->735|2823->832|2874->862|2903->863|2978->910|3007->911|3043->920|3072->921|3101->922|3134->927|3163->928|3204->941|3265->975|3294->976|3326->981|3354->982|3387->988|3467->1041|3496->1042|3532->1051|3578->1069|3607->1070|3636->1071|3678->1085|3707->1086|3744->1096|3792->1116|3821->1117|3862->1131|3913->1161|3942->1162|4070->1263|4099->1264|4131->1269|4159->1270|4187->1271
                  LINES: 16->1|21->1|22->2|22->2|22->2|35->15|35->15|35->15|35->15|35->15|35->15|35->15|37->17|38->18|38->18|38->18|38->18|38->18|39->19|40->20|40->20|42->22|43->23|43->23|44->24|44->24|44->24|44->24|44->24|44->24|45->25|45->25|45->25|45->25|45->25|45->25|47->27|47->27|47->27|48->28|48->28|48->28|50->30|50->30|50->30|53->33|53->33|54->34|54->34|54->34|54->34|54->34|55->35|56->36|56->36|57->37|57->37|59->39|60->40|60->40|61->41|61->41|61->41|61->41|61->41|61->41|63->43|63->43|63->43|64->44|64->44|64->44|68->48|68->48|69->49|69->49|70->50
                  -- GENERATED --
              */
          