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
    *
    * @param gamma
    * @param s
    * @tparam G
    * @return
    */
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
    var updated = gamma

    updated = updated
        .addCombinator(new GameName(s.name))
        //.addCombinator(new IdForGame(pygames.castle))
        //.addCombinator(new Structure())
        .addCombinator(new CreateGameMethod(s))
        .addCombinator(new PythonStructure(processDeal(s), game(pysol.startGame)))

    updated = constructHelperClasses(updated, s)

    updated
  }



//
//  def captureStructure(s:Solitaire): Python = {
//    val name = s.name
//
//    // get constraints
//    var code =
//      s"""
//         |# Build up local stack to handle Foundations
//         |class ${name}_FoundationStack(AbstractFoundationStack):
//         |    def acceptsCards(self, from_stack, cards):
//         |        if not AbstractFoundationStack.acceptsCards(self, from_stack, cards):
//         |            return False
//         |        if self.cards:
//         |            # check the rank
//         |            if (self.cards[-1].rank + self.cap.dir) % self.cap.mod != cards[0].rank:
//         |                return False
//         |        return True
//       """.stripMargin
//  }



  /**
    * Construct Python statements to handle deal.
    *
    * @param s
    * @return
    */
  def processDeal(s:Solitaire): Python = {

    var stmts = ""
    for (step <- s.getDeal.asScala) {
      step match {
        case f: FilterStep => {
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

        }

        // frames=0 means do no animation during the deal.
        case d: DealStep => {
          println("Deal step:" + d)
          val payload = d.payload
          val flip:String = if (payload.faceUp) { "1" } else { "0" }
          val numCards = payload.numCards
            d.target match {
              case ct:ContainerTarget => {
                ct.targetType match {
                  case SolitaireContainerTypes.Foundation => {
                    stmts = stmts +
                            s"""
                             |for _ in range($numCards):
                             |    self.s.talon.dealRow(rows=self.s.foundations, flip=$flip, frames=0)
                             """.stripMargin
                  }
                  case SolitaireContainerTypes.Tableau => {
                    stmts = stmts +
                            s"""
                             |for _ in range($numCards):
                             |    self.s.talon.dealRow(rows=self.s.rows, flip=$flip, frames=0)
                             """.stripMargin
                  }
                  case SolitaireContainerTypes.Waste => {
                    stmts = stmts +
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=[self.s.waste], flip=$flip, frames=0)
                             """.stripMargin
                  }
                }
              }
                // just reach out to one in particular
              case et:ElementTarget => {
                val idx = et.idx

                et.targetType match {
                  case SolitaireContainerTypes.Foundation => {
                    stmts = stmts +
                      s"""
                       |for _ in range($numCards):
                       |    self.s.talon.dealRow(rows=[self.s.foundations[$idx]], flip=$flip, frames=0)
                       """.stripMargin
                  }
                  case SolitaireContainerTypes.Tableau => {
                    stmts = stmts +
                      s"""
                       |for _ in range($numCards):
                       |    self.s.talon.dealRow(rows=[self.s.rows[$idx]], flip=$flip, frames=0)
                        """.stripMargin
                  }
                  case SolitaireContainerTypes.Waste => {
                    stmts = stmts +
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=[self.s.waste[$idx]], flip=$flip, frames=0)
                        """.stripMargin
                  }
                }
                // just a single element
              }
            }
        }
      }
    }

   stmts = "def startGame(self):" + Python(stmts).indent.toString()

   Python(stmts)
  }


//  /**
//    * How to infer this structure from the existing games is seriously tricky. Instead, I would
//    * like to generate raw, given constraints.
//    */
//  class Structure {
//    def apply: Python = Python(
//      s"""|shallHighlightMatch = Game._shallHighlightMatch_RK
//          |
//          |Foundation_Class = SS_FoundationStack
//          |RowStack_Class = SuperMoveRK_RowStack
//        """.stripMargin)
//
//    val semanticType:Type = game(pysol.structure)
//  }

//
//  /**
//    * Manually deals with cards...
//    */
//  class PlaceAcesMethod {
//    def apply:Python = Python(
//      s"""
//         |def startGame(self):
//         |		aces = []
//         |		for i in range(51,-1,-1):
//         |			cd = self.s.talon.cards[i]
//         |			if cd.rank == 0:
//         |				aces.append(cd)
//         |				del self.s.talon.cards[i]
//         |		for cd in aces:
//         |			self.s.talon.cards.append(cd)
//         |		aces=[]
//         |		self.s.talon.dealRow(rows=self.s.foundations)
//         |		for i in range(6):
//         |			self.s.talon.dealRow(frames=0)
//       """.stripMargin)
//
//    val semanticType:Type = game(pysol.startGame)
//  }

//  /**
//    * This uses the _shuffleHook capability to pull to the end the ACES, which are then dealt
//    * last to the foundation. Relies on domain knowledge of PySol
//    */
//  class StartGameMethod {
//    def apply:Python = Python(
//      s"""
//         |def _shuffleHook(self, cards):
//         |    # move Aces to bottom of the Talon (i.e. last cards to be dealt)
//         |    return self._shuffleHookMoveToBottom(cards, lambda c: (c.rank == 0, c.suit))
//         |
//         |def startGame(self):
//         |    for i in range(6):
//         |        self.s.talon.dealRow(frames=0)
//         |    #self.startDealSample()
//         |
//         |    # Final aces go out
//         |    self.s.talon.dealRow(rows=self.s.foundations)
//       """.stripMargin)
//
//    val semanticType:Type = game(pysol.startGame)
//  }

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
           |
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


  @combinator object InitIndex {
    def apply(name:String) : (Python, Path) = {
      val code =
        Python(s"""
                 |#!/usr/bin/env python
                 |## bring in newly generated games here...
                 |##---------------------------------------------------------------------------##
                 |import ${name}
                 |""".stripMargin)
      (code, Paths.get("__init__.py"))
    }

    val semanticType:Type = game(pysol.fileName) =>: game(pysol.initFile)
  }

  @combinator object makeMain {
    def apply(name:Python, id: Python, fileName:String, classDefs:Python, structure:Python, createGame: Python, startGame: Python): (Python, Path) = {
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
                 |
                 |# ************************************************************************
                 |# * $name
                 |# ************************************************************************
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
    val semanticType:Type = variationName =>:
      gameID =>:
      game(pysol.fileName) =>:
      game(pysol.classes) =>:
      game(pysol.structure) =>:
      game(pysol.createGame) =>:
      game(pysol.startGame) =>:
      game(complete)
  }


}