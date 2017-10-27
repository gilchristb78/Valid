package org.combinators.solitaire.castle

import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain.constraints._
import domain.moves.SingleCardMove
import org.combinators.solitaire.shared.{GameTemplate, Score52}

// domain
import domain._
import domain.ui._
import domain.castle._

trait game extends GameTemplate with Score52 {

  /**
    * Given an empty Solitaire domain object, construct the structure of Klondike using a WastePile,
    * Stock, Foundation and Tableau.
    */
  @combinator object CellStructure {
    def apply(s: Solitaire, t: Tableau, f:Foundation, st:Stock): Solitaire = {
      s.setTableau(t)
      s.setFoundation(f)
      s.setStock(st)
      s
    }

    val semanticType: Type =
      'Solitaire ('Tableau ('None)) :&: 'Solitaire ('Layout ('None)) :&: 'Solitaire ('Rules('None)) =>:
        'Tableau ('Valid :&: 'Eight :&: 'Row) =>:
        'Foundation ('Valid :&: 'Four :&: 'Pile) =>:
        'Stock ('Valid :&: 'One :&: 'Deck) =>:
        'Solitaire ('Structure ('Castle))
  }

  @combinator object FourPileFoundation extends NPileFoundation(4, 'Four)
  @combinator object EightRowTableau extends NRowTableau(8, 'Eight)


  /**
    * Class for constructing Tableau from n Piles.
    *
    * @param n            number of piles to create
    * @param nAsType      type of Pile within the semantic type 'Tableau ('Valid :&: typ)
    */
  class NRowTableau(n: Int, nAsType: Type) {
    def apply(): Tableau = {
      val t = new Tableau()
      for (_ <- 1 to n)
        t.add(new Row())
      t
    }

    val semanticType: Type = 'Tableau ('Valid :&: nAsType :&: 'Row)
  }

  @combinator object Construction {
    def apply(s: Solitaire, rules: Rules, layout:Layout): Solitaire = {
      s.setLayout(layout)
      s.setRules(rules)

      s
    }

    val semanticType: Type =
      'Solitaire ('Structure ('Castle)) =>:
        'Rules('Castle) =>:
        'Layout ('Valid :&: 'Castle) =>:
        'Variation('Castle)
  }


  /**
    * Common layout for solitaire games with just Stcok on left and tableau on right.
    */
  @combinator object CastleLayout {
    def apply(): Layout = {
      val lay = new Layout()

      lay
    }

    val semanticType: Type = 'Layout ('Valid :&: 'Castle)
  }

  // HACK: non-compositional. Also embeds UI indications here as well,
  // but that is just a convenience. Can be moved elsewhere.
  @combinator object Rules {
    def apply(solitaire:Solitaire): Rules = {
      val rules = new Rules()

      val tableau = solitaire.getTableau

      val foundation= solitaire.getFoundation

      val truth = new ReturnConstraint (new ReturnTrueExpression)
      val falsehood = new ReturnConstraint (new ReturnFalseExpression)
      val isEmpty = new ElementEmpty ("destination")

      val if_move = new IfConstraint(isEmpty)
      val moveCheck = new IfConstraint(new NextRank("destination.peek()", "movingCard"))

      // Tableau to Tableau
      val tableauToTableau = new SingleCardMove("MoveCard", tableau, tableau, moveCheck)
      rules.addDragMove(tableauToTableau)

      //tableau to foundations
      val combined = new IfConstraint(new NextRank("movingCard", "destination.peek()"),
        new IfConstraint(new SameSuit("movingCard", "destination.peek()")),
        falsehood)

      val tableauToFoundation = new SingleCardMove("BuildCard", tableau, foundation, combined)
      rules.addDragMove(tableauToFoundation)

      rules
    }

    val semanticType:Type = 'Solitaire('Structure('Castle)) =>: 'Rules('Castle)
  }



}
