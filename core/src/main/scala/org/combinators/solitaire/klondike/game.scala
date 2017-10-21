package org.combinators.solitaire.klondike

import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared.{GameTemplate, Score52}

// domain
import domain._
import domain.ui._
import domain.moves._
import domain.constraints._

trait game extends GameTemplate with Score52 {
//
//  /**
//    * Given an empty Solitaire domain object, construct the structure of Klondike using a WastePile,
//    * Stock, Foundation and Tableau.
//    */
//  @combinator object CellStructure {
//    def apply(s: Solitaire, t: Tableau, st: Stock, f:Foundation, w:Waste): Solitaire = {
//      s.setTableau(t)
//      s.setStock(st)
//      s.setFoundation(f)
//      s.setWaste(w)
//
//      s
//    }
//
//    val semanticType: Type =
//      'Solitaire ('Tableau ('None)) :&: 'Solitaire ('Layout ('None)) :&: 'Solitaire ('Rules('None)) =>:
//        'Tableau ('Valid :&: 'Seven :&: 'BuildablePile) =>:
//        'Stock ('Valid :&: 'One :&: 'Deck) =>:
//        'Foundation ('Valid :&: 'Four :&: 'Pile) =>:
//        'Waste ('Valid :&: 'One :&: 'Pile) =>:
//        'Solitaire ('Structure ('Klondike))
//  }
//
//  @combinator object SevenBPTableau extends NBuildablePileTableau(7, 'Seven)
//
//  // 4-HomePile Foundation
//  @combinator object WastePileStructure {
//    def apply(): Waste = {
//      val r = new Waste()
//
//      r.add(new WastePile())
//
//      println("setting single pile WastePile")
//
//      r
//    }
//
//    val semanticType: Type = 'Waste ('Valid :&: 'One :&: 'Pile)
//  }
//
//
////  // 4-HomePile Foundation
////  @combinator object FourHomePile {
////    def apply(): Foundation = {
////      val r = new Foundation()
////
////      r.add(new Pile()) // put into for-loop soon.
////      r.add(new Pile())
////      r.add(new Pile())
////      r.add(new Pile())
////
////      println("setting four Pile Foundation")
////
////      r
////    }
////
////    val semanticType: Type = 'Foundation ('Valid :&: 'Four :&: 'Pile)
////  }
//
//  @combinator object Construction {
//    def apply(s: Solitaire, rules: Rules, layout:Layout): Solitaire = {
//      s.setLayout(layout)
//      s.setRules(rules)
//
//      s
//    }
//
//    val semanticType: Type =
//      'Solitaire ('Structure ('Klondike)) =>:
//        'Rules('Klondike) =>:
//        'Layout ('Valid :&: 'Klondike) =>:
//        'Variation('Klondike)
//  }
//
//
//  /**
//    * Common layout for solitaire games with just Stcok on left and tableau on right.
//    */
//  @combinator object KlondikeLayout {
//    def apply(): Layout = {
//      val lay = new Layout()
//      lay.add(Layout.Stock, 15, 20, 73, 97)
//      lay.add(Layout.WastePile, 95, 20, 73, 97)
//      lay.add(Layout.Foundation, 293, 20, 680, 97)
//      lay.add(Layout.Tableau, 40, 200, 1360, 13*97)
//
//      lay
//    }
//
//    val semanticType: Type = 'Layout ('Valid :&: 'Klondike)
//  }
//
//  // HACK: non-compositional. Also embeds UI indications here as well,
//  // but that is just a convenience. Can be moved elsewhere.
//  @combinator object Rules {
//    def apply(solitaire:Solitaire): Rules = {
//      val rules = new Rules()
//
//      /**
//        * Rules to be devised.
//        * 1. Deal three (1) to waste pile
//        * 2. Flip top card
//        * 3. Move card to foundation
//        * 4. Move column to tableau
//        * 5. Move King to empty buildablePile
//        */
//
//      val tableau = solitaire.getTableau
//      val found   = solitaire.getFoundation
//      val stock   = solitaire.getStock
//      val waste   = solitaire.getWaste
//
//      val truth = new ReturnConstraint (new Truth)
//      val falsehood = new ReturnConstraint (new Falsehood)
//      val isEmpty = new IsEmpty ("destination")
//
//      val place = new IfConstraint(new NextRank("destination.peek()", "movingColumn.peek(0)"),
//        new IfConstraint(new OppositeColor("movingColumn.peek(0)", "destination.peek()")),
//        falsehood)
//      val place2 = new IfConstraint(new NextRank("destination.peek()", "movingCard"),
//        new IfConstraint(new OppositeColor("movingCard", "destination.peek()")),
//        falsehood)
//
//      // Tableau to tableau (includes to empty card)
//      val c = new IfConstraint(isEmpty,
//        new IfConstraint(new ExpressionConstraint("movingColumn.peek(0).getRank()", "==", "13")),   // only allow kings to move to empty piles
//        place)
//      val c2 = new IfConstraint(isEmpty,
//        new IfConstraint(new ExpressionConstraint("movingCard.getRank()", "==", "13")),   // only allow kings to move to empty piles
//        place2)
//
//      val tableauToTableau = new ColumnMove("MoveColumn", tableau, tableau, c)
//      rules.addDragMove(tableauToTableau)
//      val wasteToTableau = new SingleCardMove("MoveCard", waste, tableau, c2)
//      rules.addDragMove(wasteToTableau)
//
//      // Flip a face-down card on Tableau.
//      val faceDown =
//        new BooleanExpression("!source.peek().isFaceUp()")
//      val tableauFlip = new FlipCardMove("FlipCard", tableau, new IfConstraint(faceDown))
//      rules.addPressMove(tableauFlip)
//
//      // Move to foundation from Tableau
//      val aCard = new Card
//      val if3 =
//        new IfConstraint(isEmpty,
//          new IfConstraint(new IsAce(aCard, "movingCard")),
//          new IfConstraint(new NextRank("movingCard", "destination.peek()"),
//            new IfConstraint(new SameSuit("movingCard", "destination.peek()")),
//            falsehood)
//        )
//
//      val aColumn = new Column
//      val if2 =
//        new IfConstraint(isEmpty,
//          new IfConstraint(new IsAce(aColumn, "movingColumn")),
//          new IfConstraint (new ExpressionConstraint("movingColumn.count()", ">", "1"),
//            falsehood,
//            new IfConstraint(new NextRank("movingColumn.peek(0)", "destination.peek()"),
//              new IfConstraint(new SameSuit("movingColumn.peek(0)", "destination.peek()")),
//              falsehood)
//          )
//        )
//
//      // build on the foundation, from tableau and waste
//      val buildFoundation = new ColumnMove("BuildFoundation", tableau, found, if2)
//      rules.addDragMove(buildFoundation)
//      val buildFoundationFromWaste = new SingleCardMove("BuildFoundationFromWaste", waste, found, if3)
//      rules.addDragMove(buildFoundationFromWaste)
//
//      // Deal card from deck
//      val deck_move = new IfConstraint(new IsEmpty ("source"),
//        falsehood, truth)
//      val deckDeal = new DeckDealMove("DealDeck", stock, waste, deck_move)
//      rules.addPressMove(deckDeal)
//
//      // reset deck if empty. Move is triggered by press on stock.
//      // this creates DeckToPile, as in the above DeckDealMove.
//      val reset_move = new IfConstraint(new IsEmpty ("source"),
//        truth, falsehood)
//      val deckReset = new ResetDeckMove("ResetDeck", stock, waste, reset_move)
//      rules.addPressMove(deckReset)
//
//
//      rules
//    }
//
//    val semanticType:Type = 'Solitaire('Structure('Klondike)) =>: 'Rules('Klondike)
//  }

}
