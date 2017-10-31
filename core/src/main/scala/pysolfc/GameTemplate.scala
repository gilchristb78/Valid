package pysolfc

import com.github.javaparser.ast.expr.Name
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.{ReflectedRepository, combinator}
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.{Java, Python}
import domain.{Container, Solitaire}
import org.combinators.solitaire.shared.{Base, PythonSemanticTypes, SolitaireDomain}


trait GameTemplate extends Base with PythonSemanticTypes {

  /**
    * Opportunity to customize based on solitaire domain object.
    * @param gamma
    * @param s
    * @tparam G
    * @return
    */
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
    val updated = gamma

    updated
      .addCombinator(new GameName("Custom" + s.getClass.getSimpleName))
      .addCombinator(new IdForGame(99232))
      .addCombinator(new Structure())
      .addCombinator(new CreateGameMethod())
      .addCombinator(new StartGameMethod())

  }

  class Structure {
    def apply: Python = Python(
      s"""|shallHighlightMatch = Game._shallHighlightMatch_RK
          |
          |Foundation_Class = SS_FoundationStack
          |RowStack_Class = SuperMoveRK_RowStack
        """.stripMargin)

    val semanticType:Type = game(pysol.structure)
  }

  class StartGameMethod {
    def apply:Python = Python(
      s"""
         |def _shuffleHook(self, cards):
         |    # move Aces to bottom of the Talon (i.e. last cards to be dealt)
         |    return self._shuffleHookMoveToBottom(cards, lambda c: (c.rank == 0, c.suit))
         |
         |def startGame(self):
         |    for i in range(4):
         |        self.s.talon.dealRow(frames=0)
         |    self.startDealSample()
         |    for i in range(2):
         |        self.s.talon.dealRow()
         |    self.s.talon.dealRow(rows=self.s.foundations)
       """.stripMargin)

    val semanticType:Type = game(pysol.startGame)
  }

  class CreateGameMethod {
    def apply(view:Python) :Python = Python(
      s"""
         |def createGame(self, playcards=13):
         |        # create layout
         |        l, s = Layout(self), self.s
         |
         |        # set window
         |        # (set size so that at least 13 cards are fully playable)
         |        w = max(3*l.XS, l.XS+(playcards-1)*l.XOFFSET)
         |        x0 = l.XM
         |        x1 = x0 + w + 2*l.XM
         |        x2 = x1 + l.XS + 2*l.XM
         |        x3 = x2 + w + l.XM
         |        h = l.YM + 4*l.YS
         |        self.setSize(x3, h)
         |
         |        ${view.indent.indent.getCode}
         |
         |        x, y = self.width - l.XS, self.height - l.YS
         |        s.talon = InitialDealTalonStack(x, y, self)
         |
         |        # default
         |        l.defaultAll()
       """.stripMargin)

    val semanticType:Type = game(game.view) =>: game(pysol.createGame)
  }

  /**
    * Convert ID into string.
    *
    * @param id
    */
  class IdForGame(id:Int) {
    def apply: Python = Python(id.toString)

    val semanticType:Type = gameID
  }

  /** Define the class name from domain model. */
  class GameName(s:String) {
    def apply: Python = Python(s)

    val semanticType:Type = variationName
  }
//
//  //    @(GameID:Python,
//  //      Structure:Python,
//  //      Hilight:Python,
//  //      Shufflehook:Python,
//  // //CreateGameParams)
//  @combinator object ByTemplate {
//    def apply(id:Python,
//              structure:Python,
//              hilight:Python,
//              shuffle:Python,
//              create: Python): Python =
//      py.template.render(structure, hilight, shuffle, create, id)
//
//    val semanticType = 'GameId =>:
//      'Structure =>:
//      'Hilight =>:
//      'Shuffle =>:
//      'CreateGame =>:
//      'Program
//  }

  @combinator object makeMain {
    def apply(name:Python, id: Python, structure:Python, createGame: Python, startGame: Python): Python =
      Python(s"""|__all__ = []
                 |
                 |# imports
                 |import sys
                 |
                 |# PySol imports
                 |from pysollib.gamedb import registerGame, GameInfo, GI
                 |from pysollib.util import *
                 |from pysollib.mfxutil import kwdefault
                 |from pysollib.stack import *
                 |from pysollib.game import Game
                 |from pysollib.layout import Layout
                 |from pysollib.pysoltk import MfxCanvasText
                 |
                 |
                 |# ************************************************************************
                 |# * $name
                 |# ************************************************************************
                 |
                 |class $name(Game):
                 |
                 |${structure.indent.getCode}
                 |
                 |${createGame.indent.getCode}
                 |
                 |${startGame.indent.getCode}
                 |
                 |# register the game
                 |registerGame(GameInfo($id, $name, "$name", GI.GT_1DECK_TYPE, 1, 0, GI.SL_MOSTLY_SKILL))
                 |
                 """.stripMargin)

    val semanticType:Type = variationName =>: gameID =>: game(pysol.structure) =>: game(pysol.createGame) =>: game(pysol.startGame) =>: game(complete)
  }


}