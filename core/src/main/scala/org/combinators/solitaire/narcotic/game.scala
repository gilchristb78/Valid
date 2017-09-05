package org.combinators.solitaire.narcotic

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

  // Narcotic is an example solitaire game that uses Deck and Tableau.
  @combinator object NarcoticCellStructure {
    def apply(s: Solitaire, t: Tableau, st: Stock): Solitaire = {
      s.setTableau(t)
      s.setStock(st)

      s
    }

    val semanticType: Type =
      'Solitaire ('Tableau ('None)) :&: 'Solitaire ('Layout ('None)) :&: 'Solitaire ('Rules('None)) =>:
        'Tableau ('Valid :&: 'Four :&: 'Pile) =>:
        'Stock ('Valid :&: 'OneDeck) =>:
        'Solitaire ('Structure ('Narcotic))
  }


  @combinator object NarcoticConstruction {
    def apply(s: Solitaire, rules: Rules, layout:Layout): Solitaire = {
      s.setLayout(layout)
      s.setRules(rules)

      s
    }

    val semanticType: Type =
      'Solitaire ('Structure ('Narcotic)) =>:
        'Rules('Narcotic) =>:
        'Layout ('Valid :&: 'StockTableau) =>:   
        'Variation('Narcotic)
  }

  // This should be moved to shared area rather than being copied.
  @combinator object SingleDeckStock {
    def apply(): Stock = new Stock()

    val semanticType: Type = 'Stock ('Valid :&: 'OneDeck)
  }


  // in Narcotic we need a valid tableau. Not sure why we have to
  // restrict that here to be 4; could still be searched
  @combinator object AddFourPileTableau {
    def apply(s: Solitaire, tab: Tableau): Solitaire = {
      s.setTableau(tab)
      println("setting four-pile tableau")
      s
    }

    val semanticType: Type =
      'Solitaire ('Tableau ('None)) =>: 'Tableau ('Valid :&: 'Pile) =>:
        'Solitaire ('Tableau ('Valid :&: 'Pile))
  }

  // HACK: non-compositional. Also embeds UI indications here as well,
  // but that is just a convenience. Can be moved elsewhere.
  @combinator object NarcoticRules {
    def apply(solitaire:Solitaire): Rules = {
      val rules = new Rules()
      val tableau = solitaire.getTableau
      val stock = solitaire.getStock

      val truth = new ReturnConstraint (new ReturnTrueExpression)
      val falsehood = new ReturnConstraint (new ReturnFalseExpression)
      val isEmpty = new ElementEmpty ("destination")

      val if_move = new IfConstraint(isEmpty)

      // Tableau to Tableau. Can move a card to the left if it is
      // going to a non-empty pile whose top card is the same rank
      // as moving card, and which is to the left of the source.
      val toLeftOf =
         new BooleanExpression("((org.combinators.solitaire.narcotic.Narcotic)game).toLeftOf(destination, source)")

      val sameRank = new SameRank("movingCard", "destination.peek()")
      val tt_move = new IfConstraint (new ElementEmpty("destination"),
		      falsehood,
		    new IfConstraint(toLeftOf,
		      new IfConstraint (sameRank),
		    falsehood)
		    )

      val tableauToTableau = new SingleCardMove("MoveCard", tableau, tableau, tt_move)
      rules.addDragMove(tableauToTableau)

      // deal four cards from Stock
      val deck_move = new IfConstraint(new ElementEmpty ("source"),
          falsehood, truth)
      val deckDeal = new DeckDealMove("DealDeck", stock, tableau, deck_move)
      println ("stock:" + stock.getClass() + ", tableau:" + tableau)
      rules.addPressMove(deckDeal)

      // reset deck if empty. Move is triggered by press on stock.
      // this creates DeckToPile, as in the above DeckDealMove.
      val reset_move = new IfConstraint(new ElementEmpty ("source"),
          truth, falsehood)
      val deckReset = new ResetDeckMove("ResetDeck", stock, tableau, reset_move)
      rules.addPressMove(deckReset)

      rules
   }

   val semanticType:Type = 'Solitaire('Structure('Narcotic)) =>: 'Rules('Narcotic)
  }



}
