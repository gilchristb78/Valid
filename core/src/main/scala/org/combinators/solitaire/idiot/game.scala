package org.combinators.solitaire.idiot

import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared.{GameTemplate, Score52}

// domain
import domain._
import domain.constraints._
import domain.moves._
import domain.ui._

trait Game extends GameTemplate with Score52 {

  // force you to use the proper arity type.. and avoid typos.
  object typeDeclarations {
    val column:Type = 'Column
    def constr(a:Type, b:Type):Type = 'ParamColumn (a, b)
    val p1:Type = 'P1
    def p2(a:Type):Type = 'P2(a)

  }

  import typeDeclarations._

  /**
    * Given an empty Solitaire domain object, construct the structure of Idiot by taking a 4-column tableau
    * and a one-deck Stock.
    */
  @combinator object IdiotCellStructure {
    def apply(s: Solitaire, t: Tableau, st: Stock): Solitaire = {
      s.setTableau(t)
      s.setStock(st)

      s
    }

    val semanticType: Type =
      'Solitaire ('Tableau ('None)) :&: 'Solitaire ('Layout ('None)) :&: 'Solitaire ('Rules('None)) =>:
        'Tableau ('Valid :&: 'Four :&: 'Column) =>:
        'Stock ('Valid :&: 'One :&: 'Deck) =>:
        'Solitaire ('Structure ('Idiot))
  }


  @combinator object IdiotConstruction {
    def apply(s: Solitaire, rules: Rules, layout:Layout): Solitaire = {
      s.setLayout(layout)
      s.setRules(rules)

      s
    }

    val semanticType: Type =
      'Solitaire ('Structure ('Idiot)) =>:
        'Rules('Idiot) =>:
        'Layout ('Valid :&: 'StockTableau) =>:     // NOTHING TO CAUSE THIS.
        'Variation('Idiot)
  }


  // HACK: non-compositional. Also embeds UI indications here as well,
  // but that is just a convenience. Can be moved elsewhere.
  @combinator object IdiotRules {
    def apply(solitaire:Solitaire): Rules = {
      val rules = new Rules()
      val tableau = solitaire.getTableau
      val stock = solitaire.getStock

      val truth = new ReturnConstraint (new ReturnTrueExpression)
      val falsehood = new ReturnConstraint (new ReturnFalseExpression)
      val isEmpty = new ElementEmpty ("destination")

      val if_move = new IfConstraint(isEmpty)

      // Tableau to Tableau
      val tableauToTableau = new SingleCardMove("MoveCard", tableau, tableau, if_move)
      rules.addDragMove(tableauToTableau)

      // this special method is added by gameDomain to be accessible here.
      val sameSuitHigherRankVisible =
        new BooleanExpression("((org.combinators.solitaire.idiot.Idiot)game).isHigher((Column)source)")

      val ifr_move = new IfConstraint (new ElementEmpty("source"),
          falsehood,
          new IfConstraint (sameSuitHigherRankVisible))

      val removeCardFromTableau = new RemoveSingleCardMove("RemoveCard", tableau, ifr_move)
      rules.addClickMove(removeCardFromTableau)

      // Remove a card from the tableau? This can be optimized by a click
      // do I allow another Rule? Or reuse existing one?
      // Not sure how to deal with MOVE with a single PRESS
      // That is, this won't be the head part of a 'drag' operation.

      // deal four cards from Stock
      val deck_move = new IfConstraint(new ElementEmpty ("source"),
        falsehood, truth)
      val deckDeal = new DeckDealMove("DealDeck", stock, tableau, deck_move)
      rules.addPressMove(deckDeal)

      rules
    }

    val semanticType:Type = 'Solitaire('Structure('Idiot)) =>: 'Rules('Idiot)
  }

  // in Idiot we need a valid tableau. Not sure why we have to
  // restrict that here to be 4; could still be searched
  @combinator object AddFourColumnTableau {
    def apply(s: Solitaire, tab: Tableau): Solitaire = {
      s.setTableau(tab)
      println("setting four-column tableau")
      s
    }

    val semanticType: Type =
      'Solitaire ('Tableau ('None)) =>: 'Tableau ('Valid :&: 'Column) =>:
        'Solitaire ('Tableau ('Valid :&: 'Column))
  }

}
