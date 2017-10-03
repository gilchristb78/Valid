package org.combinators.solitaire.archway

import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain._
import domain.archway._
import domain.constraints._
import domain.moves._
import domain.ui._
import org.combinators.solitaire.shared.{GameTemplate, Score52}

/** Sets the objects of Archway, the rules for its Moves, and the
  * placement of its Views.
  *
  * TODO: Score52...
  */
trait Game extends GameTemplate with Score52 {

  /**
    * Archway's domain structure has:
    * - Tableau in 4 columns of 12 cards each,
    * - Foundation in two parts
    *   * 4 piles of each suit of Ace,
    *   * 4 piles of each suit of King.
    * - Reserve in 11 piles, from Two to Queen.
    */
  @combinator object ArchwayStructure {
    def apply(sol: Solitaire, t: Tableau, r: Reserve, f:Foundation, c: Container): Solitaire = {
      sol.setTableau(t)
      sol.setReserve(r)
      sol.setFoundation(f)
      // NOTE: Here is defined the second part of the foundation using Solitaire's specialContainers.
      // This is to separate the controller logic. A bit hackish, but it works for now.
      sol.setContainer("KingsDownFoundation", c)
      sol
    }

    // When given an empty Solitaire type, give back the semantic structure of Archway.
    val semanticType: Type =
      ('Solitaire ('Reserve ('None) :&: 'Tableau ('None) :&: 'Foundation('None)) :&: 'Solitaire ('Layout ('None)) :&: 'Solitaire ('Rules('None))) =>:
        'Tableau ('Valid :&: 'Four :&: 'Column) =>:
        'Reserve ('Valid :&: 'Eleven :&: 'Pile) =>:
        'Foundation ('Valid :&: 'Four :&: 'AcesUpPile) =>:
        'KingsDownFoundation ('Valid :&: 'Four :&: 'KingsDownPile) =>:
        'Solitaire ('Structure ('Archway))
  }

  /**
    * Given an Archway Structure, its Rules, and its Layout,
    * return the Archway variation.
    */
  @combinator object ArchwayConstruction {
    def apply(sol: Solitaire, rules: Rules, layout: Layout): Solitaire = {
      sol.setLayout(layout)
      sol.setRules(rules)
      sol
    }

    val semanticType: Type =
      'Solitaire ('Structure ('Archway)) =>:
        'Rules('Archway) =>:
        'Layout('Valid :&: 'ArchwayLayout) =>:
        'Variation('Archway)
  }


  /** Generate Reserve */
  @combinator object ElevenPileReserve extends NPileReserve(11, 'Eleven)

  /** Generate Tableau */
  @combinator object CreateFourColumnTableau extends NColumnTableau(4, 'Four)

  /** Manually generate the Aces Foundation, because it's split between Aces and Kings.
    * The default NPileFoundation generates new Pile(), but we need AcesUpPile(), a subclass.
    */
  @combinator object ArchwayFoundation {
    def apply(): Foundation = {
      val t = new Foundation()

      for (_ <- 1 to 4)
        t.add(new AcesUpPile())

      t
    }
    val semanticType: Type = 'Foundation ('Valid :&: 'Four :&: 'AcesUpPile)
  }

  /** Manually generate the Kings Foundation, because it's split between Aces and Kings
    * See comment at AchwayFoundation. */
  @combinator object KingsDownFoundation {
    def apply(): Container = {
      val t = new Container()

      for (_ <- 1 to 4)
        t.add(new KingsDownPile())

      t
    }
    val semanticType: Type = 'KingsDownFoundation ('Valid :&: 'Four :&: 'KingsDownPile)
  }


  /** Generate Tableau. */
  @combinator object AddFourColumnTableau {
    def apply(sol: Solitaire, tab: Tableau): Solitaire = {
      sol.setTableau(tab)
      sol
    }

    val semanticType: Type =
      'Solitaire ('Tableau ('None)) =>: 'Tableau ('Valid :&: 'Column) =>:
        'Solitaire ('Tableau ('Valid :&: 'Column))
  }

  /**
    * Defines the rules of the game.
    * - Any top card from the Reserve or from the Tableau may be placed
    *   on the Foundation if it's the same suit and if it's one rank higher
    *   on the Aces side or one rank lower on the Kings side.
    * - A card from the Reserve may be moved to the Tableau if to
    *   an empty column.
    * - The Game is won when all cards are placed on the Foundation.
    *
    * Important!!
    * - The arguments to the Contraint classes (SameSuit, NextRank, etc.)
    *   are real fields/expressions in the generated code.
    * - The first argument of the Move class, i.e. `SingleCardMove(..)`, becomes
    *   the real class name of the generated move class.
    *
    *   !!! Successful compilation of Controllers is directly related to
    *       whether its domain object is included in a rule. For example,
    *       if there is a 'Controller('Reserve), but the Reserve is not
    *       mentioned in a rule, then compilation will fail.
    *
    *   !!! Additionally, there's a difference in compilation between an object
    *       which is only a source of a move, and an object which has is both
    *       the source and destination of a move. Each controller must satisfy
    *       actions for Click, Press, and Release, even if it's to be ignored.
    *
    */
  @combinator object ArchwayRules {
    def apply(solitaire: Solitaire): Rules = {
      val rules = new Rules()

      // Get Domain objects from Solitaire Object.
      val tableau = solitaire.getTableau
      val reserve = solitaire.getReserve
      val acesFoundation = solitaire.getFoundation
      val kingsFoundation = solitaire.getContainer("KingsDownFoundation")



      /* Contraint saying if the moving card has the same suit as the top-facing destination card. */
      // Note that these are real fields.
      val sameSuit = new SameSuit("movingCard", "destination.peek()")

      /* A Card can move to the Aces Foundation if the moving card is
       * one rank higher and has the same suit.
       */
      val moveToAcesCondition = new IfConstraint(
        new AndConstraint(
          new NextRank("movingCard", "destination.peek()"),
          sameSuit))

      /* A Card can move to the Kings Foundation if the moving card is
       * one rank lower and has the same suit.
       */
      val moveToKingsCondition = new IfConstraint(
        new AndConstraint(
          new NextRank("destination.peek()", "movingCard"),
          sameSuit))

      /* Add Rules */

      // Note that the string argument becomes a real classname.
      rules.addDragMove(
        new SingleCardMove("TableauToFoundation",
          tableau, acesFoundation, moveToAcesCondition
        )
      )

      rules.addDragMove(
        new SingleCardMove("ReserveToFoundation",
          reserve, acesFoundation, moveToAcesCondition
        )
      )

      rules.addDragMove(
        new SingleCardMove("TableauToKingsFoundation",
          tableau, kingsFoundation, moveToKingsCondition
        )
      )

      rules.addDragMove(
        new SingleCardMove("ReserveToKingsFoundation",
          reserve, kingsFoundation, moveToKingsCondition
        )
      )

      rules.addDragMove(
        new SingleCardMove("ReserveToTableau",
          reserve, tableau, new IfConstraint(new ElementEmpty("destination")))
      )

      rules.addDragMove(
        new SingleCardMove("TableauToTableau", tableau, tableau, new IfConstraint(new ReturnFalseExpression))
      )

      rules
    }
    val semanticType:Type = 'Solitaire('Structure('Archway)) =>: 'Rules('Archway)
  }


  /**
    * Defines Archway's placement of the Reserve, Foundations, and Tableau.
    */
  @combinator object ArchwayLayout {
    def apply(): Layout = {
      val lay = new Layout()

      val card_width = 73
      val card_height = 97

      // signature is (name, x, y, width, height)
      lay.add(Layout.Reserve,        15,   20, 12 * card_width, card_height)
      lay.add(Layout.Foundation,     15,  140,  4 * card_width, card_height)
      lay.add("KingsDownFoundation", 475, 140,  4 * card_width, card_height)
      lay.add(Layout.Tableau,        15,  260,  4 * card_width, 8 * card_height)
      lay
    }
    val semanticType: Type = 'Layout ('Valid :&: 'ArchwayLayout)
  }
}
