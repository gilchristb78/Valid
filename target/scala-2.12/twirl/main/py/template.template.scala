
package py

import org.combinators.templating.twirl.Java
import org.combinators.templating.twirl.Python
import com.github.javaparser.ast._
import com.github.javaparser.ast.body._
import com.github.javaparser.ast.comments._
import com.github.javaparser.ast.expr._
import com.github.javaparser.ast.stmt._
import com.github.javaparser.ast.`type`._

object template extends _root_.play.twirl.api.BaseScalaTemplate[org.combinators.templating.twirl.PythonFormat.Appendable,_root_.play.twirl.api.Format[org.combinators.templating.twirl.PythonFormat.Appendable]](org.combinators.templating.twirl.PythonFormat) with _root_.play.twirl.api.Template5[Python,Python,Python,Python,Python,org.combinators.templating.twirl.PythonFormat.Appendable] {

  /**/
  def apply/*1.2*/(Structure:Python,Hilight:Python,Shufflehook:Python,CreateGameParams:Python,GameID:Python):org.combinators.templating.twirl.PythonFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.92*/("""

"""),format.raw/*3.1*/("""# another try
"""),_display_(/*4.2*/Structure/*4.11*/.indent),format.raw/*4.18*/("""

"""),format.raw/*6.1*/("""#!/usr/bin/env python
# -*- mode: python; coding: utf-8; -*-
##---------------------------------------------------------------------------##
##
## Synthesized from CLS
##    shallHighlightMatch = hilight
##
##    Structure
##      Foundation_Class = SS_FoundationStack
##      RowStack_Class = SuperMoveRK_RowStack
##
##    ShuffleHook
## def _shuffleHook(self, cards):
##        # move Aces to bottom of the Talon (i.e. last cards to be dealt)
##        return self._shuffleHookMoveToBottom(cards, lambda c: (c.rank == 0, c.suit))
##
##    shallHighlightMatch = Game._shallHighlightMatch_RK
##---------------------------------------------------------------------------##

__all__ = []

# imports
import sys

# PySol imports
from pysollib.gamedb import registerGame, GameInfo, GI
from pysollib.util import *
from pysollib.mfxutil import kwdefault
from pysollib.stack import *
from pysollib.game import Game
from pysollib.layout import Layout
from pysollib.pysoltk import MfxCanvasText

# another try
"""),_display_(/*40.2*/Structure/*40.11*/.indent),format.raw/*40.18*/("""

"""),format.raw/*42.1*/("""# another try
"""),_display_(/*43.2*/Structure/*43.11*/.indent),format.raw/*43.18*/("""

"""),format.raw/*45.1*/("""# ************************************************************************
# * BeleagueredCastle
# ************************************************************************

class BeleagueredCastle(Game):
"""),_display_(/*50.2*/Structure/*50.11*/.indent),format.raw/*50.18*/("""

"""),_display_(/*52.2*/Shufflehook/*52.13*/.indent),format.raw/*52.20*/("""

    """),format.raw/*54.5*/("""#
    # game layout
    #
    def createGame(self, """),_display_(/*57.27*/CreateGameParams),format.raw/*57.43*/("""):
        # create layout
        l, s = Layout(self), self.s

        # set window
        # (set size so that at least 13 cards are fully playable)
        w = max(3*l.XS, l.XS+(playcards-1)*l.XOFFSET)
        x0 = l.XM
        x1 = x0 + w + 2*l.XM
        x2 = x1 + l.XS + 2*l.XM
        x3 = x2 + w + l.XM
        h = l.YM + (4+int(reserves!=0))*l.YS + int(texts)*l.TEXT_HEIGHT
        self.setSize(x3, h)

        # create stacks
        y = l.YM
        if reserves:
            x = x1 - int(l.XS*(reserves-1)/2)
            for i in range(reserves):
                s.reserves.append(ReserveStack(x, y, self))
                x += l.XS
            y += l.YS
        x = x1
        for i in range(4):
            s.foundations.append(self.Foundation_Class(x, y, self, suit=i, max_move=0))
            y += l.YS
        if texts:
            tx, ty, ta, tf = l.getTextAttr(None, "ss")
            tx, ty = x+tx, y-l.YS+ty
            font = self.app.getFont("canvas_default")
            self.texts.info = MfxCanvasText(self.canvas, tx, ty,
                                            anchor=ta, font=font)
        for x in (x0, x2):
            y = l.YM+l.YS*int(reserves!=0)
            for i in range(4):
                stack = self.RowStack_Class(x, y, self)
                stack.CARD_XOFFSET, stack.CARD_YOFFSET = l.XOFFSET, 0
                s.rows.append(stack)
                y += l.YS
        x, y = self.width - l.XS, self.height - l.YS
        s.talon = InitialDealTalonStack(x, y, self)
        if reserves:
            l.setRegion(s.rows[:4], (-999, l.YM+l.YS-l.CH/2, x1-l.CW/2, 999999))
        else:
            l.setRegion(s.rows[:4], (-999, -999, x1-l.CW/2, 999999))

        # default
        l.defaultAll()

    #
    # game overrides
    #
    def startGame(self):
        for i in range(4):
            self.s.talon.dealRow(frames=0)
        self.startDealSample()
        for i in range(2):
            self.s.talon.dealRow()
        self.s.talon.dealRow(rows=self.s.foundations)




# register the game
registerGame(GameInfo("""),_display_(/*121.24*/GameID),format.raw/*121.30*/(""", BeleagueredCastle, "Beleaguered Castle",
                      GI.GT_1DECK_TYPE:| GI.GT_OPEN, 1, 0, GI.SL_MOSTLY_SKILL))
"""))
      }
    }
  }

  def render(Structure:Python,Hilight:Python,Shufflehook:Python,CreateGameParams:Python,GameID:Python): org.combinators.templating.twirl.PythonFormat.Appendable = apply(Structure,Hilight,Shufflehook,CreateGameParams,GameID)

  def f:((Python,Python,Python,Python,Python) => org.combinators.templating.twirl.PythonFormat.Appendable) = (Structure,Hilight,Shufflehook,CreateGameParams,GameID) => apply(Structure,Hilight,Shufflehook,CreateGameParams,GameID)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: Tue Oct 04 13:18:10 EDT 2022
                  SOURCE: /home/ben/IdeaProjects/nextgen-solitaire/src/main/python-templates/template.scala.py
                  HASH: 947d8955b72b7c8280b94a7b6132600439e1ba75
                  MATRIX: 761->1|966->91|994->93|1034->108|1051->117|1078->124|1106->126|2133->1127|2151->1136|2179->1143|2208->1145|2249->1160|2267->1169|2295->1176|2324->1178|2555->1383|2573->1392|2601->1399|2630->1402|2650->1413|2678->1420|2711->1426|2790->1478|2827->1494|4913->3552|4941->3558
                  LINES: 16->1|21->1|23->3|24->4|24->4|24->4|26->6|60->40|60->40|60->40|62->42|63->43|63->43|63->43|65->45|70->50|70->50|70->50|72->52|72->52|72->52|74->54|77->57|77->57|141->121|141->121
                  -- GENERATED --
              */
          