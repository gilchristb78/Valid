package pysolfc.shared

import java.nio.file.{Path, Paths}

import de.tu_dortmund.cs.ls14.cls.interpreter.{ReflectedRepository, combinator}
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Python
import domain._
import domain.deal.{ContainerTarget, DealStep, ElementTarget, FilterStep}
import org.combinators.solitaire.shared.compilation.CodeGeneratorRegistry
import org.combinators.solitaire.shared.python.{ConstraintExpander, PythonSemanticTypes, constraintCodeGenerators}
import org.combinators.solitaire.shared.{Base, SolitaireDomain}

import scala.collection.JavaConverters._


trait GameTemplate extends Base with Structure with PythonSemanticTypes {


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

    updated
  }


  object generateHelper {
    /**
      * Helper method for the ConstraintHelper class
      */
    def tableau() : Python = {
      Python(s"""
                |def tableau():
                |	return solgame[0].s.rows
         """.stripMargin)
    }
  }


  class CreateGameMethod(solitaire:Solitaire) {
    def apply(view:Python) :Python = {
      val min = solitaire.getMinimumSize
      val width = min.width
      val height = min.height
      Python(
        s"""
           |def createGame(self):
           |    # create layout
           |    l, s = Layout(self), self.s
           |    solgame[0] = self    # store for access; this is a bit of HACK
           |    # set window size, based on layout domain
           |    self.setSize($width, $height)
           |
           |    ${view.indent.getCode}
           |
           |    # complete layout
           |    l.defaultAll()
       """.stripMargin)
    }

    val semanticType:Type = game(game.view) =>: game(pysol.createGame)
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


  @combinator object InitIndex {
    def apply(name:String) : (Python, Path) = {
      val code =
        Python(s"""
                 |#!/usr/bin/env python
                 |## bring in newly generated games here...
                 |##---------------------------------------------------------------------------##
                 |import $name
                 |""".stripMargin)
      (code, Paths.get("__init__.py"))
    }

    val semanticType:Type = game(pysol.fileName) =>: game(pysol.initFile)
  }

  class ProcessDeal(s:Solitaire) {

    def apply(generators: CodeGeneratorRegistry[Python]): Python = {
      var stmts = ""
      for (step <- s.getDeal.asScala) {
        step match {
          case f: FilterStep =>
            val app = new ConstraintExpander(f.constraint, 'Intermediate)  // No symbol really needed. Could be anything
            val filterexp = app.apply(constraintCodeGenerators.generators)

            stmts = stmts +
              s"""
                 |tmp = []
                 |for i in range(51,-1,-1):
                 |    card = self.s.talon.cards[i]
                 |    if $filterexp:
                 |        tmp.append(card)
                 |        del self.s.talon.cards[i]
                 |tmp.sort(reverse=True, key=lambda c: c.suit)    # properly sorts as C/S/D/H
                 |for cd in tmp:
                 |    self.s.talon.cards.append(cd)
                 |del tmp
                 |""".stripMargin



          // frames=0 means do no animation during the deal.
          case d: DealStep =>
            println("Deal step:" + d)
            val payload = d.payload
            val flip:String = if (payload.faceUp) { "1" } else { "0" }
            val numCards = payload.numCards
            d.target match {
              case ct:ContainerTarget =>
                ct.targetType match {
                  case SolitaireContainerTypes.Foundation =>
                    stmts = stmts +
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=self.s.foundations, flip=$flip, frames=0)
                             """.stripMargin

                  case SolitaireContainerTypes.Tableau =>
                    stmts = stmts +
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=self.s.rows, flip=$flip, frames=0)
                             """.stripMargin

                  case SolitaireContainerTypes.Waste =>
                    stmts = stmts +
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=[self.s.waste], flip=$flip, frames=0)
                             """.stripMargin

                }


              // just reach out to one in particular
              case et:ElementTarget =>
                val idx = et.idx

                et.targetType match {
                  case SolitaireContainerTypes.Foundation =>
                    stmts = stmts +
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=[self.s.foundations[$idx]], flip=$flip, frames=0)
                       """.stripMargin

                  case SolitaireContainerTypes.Tableau =>
                    stmts = stmts +
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=[self.s.rows[$idx]], flip=$flip, frames=0)
                        """.stripMargin

                  case SolitaireContainerTypes.Waste =>
                    stmts = stmts +
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=[self.s.waste[$idx]], flip=$flip, frames=0)
                        """.stripMargin

                }
                // just a single element

            }

        }
      }

      stmts = "def startGame(self):" + Python(stmts).indent.toString()

      Python(stmts)
    }

    val semanticType:Type = constraints(constraints.generator) =>: game(pysol.startGame)
  }

  @combinator object makeMain {
    def apply(name:String, id: String, fileName:String,
              helperMethods:Python,
              classDefs:Python, structure:Python, createGame: Python,
              startGame: Python): (Python, Path) = {
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
                 |# register the game (for now assume 1-deck card games)
                 |registerGame(GameInfo($id, $name, "My$name", GI.GT_1DECK_TYPE, 1, 0, GI.SL_MOSTLY_SKILL))
                 |
                 """.stripMargin)
      (code, Paths.get(fileName + ".py"))
    }
    val semanticType:Type = variationName =>: gameID =>: game(pysol.fileName) =>:
      constraints(constraints.methods) =>: game(pysol.classes) =>:
      game(pysol.structure) =>: game(pysol.createGame) =>:
      game(pysol.startGame) =>:
      game(complete)
  }


}