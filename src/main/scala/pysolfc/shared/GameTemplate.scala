package pysolfc.shared

import java.nio.file.{Path, Paths}

import org.combinators.cls.interpreter.{ReflectedRepository, combinator}
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.domain.{Stock, StockContainer, _}
import org.combinators.templating.twirl.Python
import org.combinators.solitaire.shared.python.PythonSemanticTypes
import org.combinators.solitaire.shared._
import org.combinators.templating.persistable.PythonWithPath

trait GameTemplate extends Base with Initialization with Structure with DealLogic with PythonSemanticTypes {

  /**
    * Opportunity to customize based on solitaire domain object.
    */
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
    var updated = gamma

    updated = updated
      .addCombinator(new GameName(s.name))
      .addCombinator(new CreateGameMethod(s))
      .addCombinator(new ProcessDeal(s))

    updated = constructHelperClasses(updated, s)

    updated = updated
      .addCombinator (new MakeMain(s))
    updated
  }

  /**
    * Convert ID into string.
    */
  class IdForGame(id:Int) {
    def apply: String = id.toString

    val semanticType:Type = gameID
  }


  /** Define the class name from domain model. */
  class GameName(s:String) {
    def apply: String = s

    val semanticType:Type = variationName
  }


  object generateHelper {
    /**
      * Helper method for the ConstraintHelper class
      */
    def tableau() : Python = {
      Python(s"""
                |def tableau():
                |    return solgame[0].s.rows
         """.stripMargin)
    }

    /**
      * Helper method for the ConstraintHelper class. Note that waste is a singleton so this has to be returned as a list for
      * being used with existing classes.
      */
    def waste() : Python = {
      Python(s"""
                |def waste():
                |    return [solgame[0].s.waste]
         """.stripMargin)
    }
  }


  class CreateGameMethod(solitaire:Solitaire) {
    def apply(view:Python) :Python = {
      val (width, height) = solitaire.layout.minimumSize

      var localDefs = ""
      var extraDefs = ""
      for ((ct,elements) <- solitaire.structure) {
        ct match {
          case Foundation =>
          case Tableau =>
          case StockContainer =>
          case Waste =>
          case Reserve =>
          case _ =>
            localDefs = localDefs + s"self.${ct.name} = []"
            extraDefs = extraDefs + s"""
                                       |self.sg.dropstacks = self.sg.dropstacks + self.${ct.name}
                                       |self.sg.openstacks = self.sg.openstacks + self.${ct.name}
                                     """.stripMargin
        }
      }


      val locals = Python(localDefs)
      val extra = Python(extraDefs)

      Python(
        s"""
           |def createGame(self):
           |    # create layout
           |    l, s = Layout(self), self.s
           |    solgame[0] = self    # store for access; this is a bit of HACK
           |    # set window size, based on layout domain
           |    self.setSize($width, $height)
           |
           |    # Here is where one needs to define non-standard containers
           |${locals.indent.getCode}
           |
           |${view.indent.getCode}
           |
           |    # in case cards need to vanish
           |    gx, gy = self.getInvisibleCoords()
           |    self.garbage = WasteStack(gx, gy, self, max_accept=999999, max_cards=999999)
           |
           |    # complete layout
           |    l.defaultAll()
           |${extra.indent.getCode}
           |
       """.stripMargin)
    }

    val semanticType:Type = game(game.view) =>: game(pysol.createGame)
  }


  @combinator object InitIndex {
    def apply(name:String) : PythonWithPath = {
      val code =
        Python(s"""
                  |#!/usr/bin/env python
                  |## bring in newly generated games here...
                  |##---------------------------------------------------------------------------##
                  |import $name
                  |""".stripMargin)
      PythonWithPath(code, Paths.get("__init__.py"))
    }

    val semanticType:Type = game(pysol.fileName) =>: game(pysol.initFile)
  }


  class MakeMain(sol:Solitaire) {
    def apply(name:String, id: String, fileName:String,
              helperMethods:Python,
              classDefs:Python, structure:Python, createGame: Python,
              startGame: Python): PythonWithPath = {

      val defaultDeck = 1
      var numDecks = 0
      // 0 = invisible; 1 = just deal once; -1 means infinite redeals
      var deckArrangement = 1

      sol.structure.filter(pair => pair._1 == StockContainer).foreach(pair =>
        if (sol.layout.isVisible(pair._1)) {
          pair._2.head match {
            case Stock(nd) =>
              numDecks = nd
              if (!sol.layout.isVisible(pair._1)) {
                deckArrangement = 0
              } else {
                deckArrangement = -1   // HACK: Support unlimited redeals. need to place this info in KlondikeDomain
            }
          }
        } else {
          pair._2.head match {     // even if not visible, must update numDecks
            case Stock (nd) =>
              numDecks = nd
          }
        }
      )

      if (numDecks == 0) { numDecks = defaultDeck }

      val code =
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
                   |# stored instance created, for use by helper methods
                   |solgame = [None]
                   |
                   |def garbage():
                   |    return solgame[0].garbage
                   |
                   |
                   |# ************************************************************************
                   |# * $name
                   |# ************************************************************************
                   |
                   |${helperMethods.getCode}
                   |
                   |${classDefs.getCode}
                   |
                   |class $name(Game):
                   |
                   |${structure.indent.getCode}
                   |
                   |${createGame.indent.getCode}
                   |
                   |${startGame.indent.getCode}
                   |
                   |# register the game (the fifth parameter records number of decks)
                   |# The sixth parameter is the number of redeals allowed. Make -1 to be maximally flexible and
                   |# depend on logic to deny. If 0 then invisible deck.
                   |registerGame(GameInfo($id, $name, "My$name", GI.GT_1DECK_TYPE, $numDecks, $deckArrangement, GI.SL_MOSTLY_SKILL))
                   |""".stripMargin)
      PythonWithPath(code, Paths.get(fileName + ".py"))
    }
    val semanticType:Type = variationName =>: gameID =>: game(pysol.fileName) =>:
      constraints(constraints.methods) =>: game(pysol.classes) =>:
      game(pysol.structure) =>: game(pysol.createGame) =>:
      game(pysol.startGame) =>:
      game(complete)
  }
}