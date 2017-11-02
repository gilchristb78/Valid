package pysolfc

import java.util.UUID

import de.tu_dortmund.cs.ls14.cls.interpreter.{ReflectedRepository, combinator}
import de.tu_dortmund.cs.ls14.cls.types.{Constructor, Type}
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.{Java, Python}
import domain.constraints.OrConstraint
import domain._
import domain.deal.{ContainerTarget, DealStep, ElementTarget, FilterStep}
import domain.moves.{ColumnMove, SingleCardMove}
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
        .addCombinator(new IdForGame(pygames.castle))
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
          val app = new ConstraintExpander(f.constraint, 'Intermediate)
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

  /**
    * NO special constraints just yet
    */
  @combinator object DefaultGenerator {
    def apply: CodeGeneratorRegistry[Python] = constraintCodeGenerators.generators
    val semanticType: Type = constraints(constraints.generator)
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
           |        self.setSize($width, $height)
           |
         |        ${view.indent.indent.getCode}
           |
         |        # deck that exists only to deal out cards
           |        x, y = self.width - l.XS, self.height - l.YS
           |        s.talon = InitialDealTalonStack(x, y, self)
           |
         |        # default
           |        l.defaultAll()
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

  @combinator object InitIndex {
    def apply() : Python = {
      Python(
        s"""
           |#!/usr/bin/env python
           |## bring in newly generated games here...
           |##---------------------------------------------------------------------------##
           |import castle
           |""".stripMargin)
    }

    val semanticType:Type = game(pysol.initFile)
  }

  @combinator object makeMain {
    def apply(name:Python, id: Python, classDefs:Python, structure:Python, createGame: Python, startGame: Python): Python = {


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
                 |# register the game
                 |registerGame(GameInfo($id, $name, "My$name", GI.GT_1DECK_TYPE, 1, 0, GI.SL_MOSTLY_SKILL))
                 |
                 """.stripMargin)
      }
    val semanticType:Type = variationName =>:
      gameID =>:
      game(pysol.classes) =>:
      game(pysol.structure) =>:
      game(pysol.createGame) =>:
      game(pysol.startGame) =>:
      game(complete)
  }


}