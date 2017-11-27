package org.combinators.solitaire.stalactites

import org.combinators.generic
import org.combinators.solitaire.shared.{GameTemplate, Score52}

trait Game extends GameTemplate with Score52 with generic.JavaCodeIdioms {
//
//  /**
//    * Stalactites's domain structure has:
//    * - Tableau in 8columns of 6 cards each,
//    * - Foundation of four piles
//    * - Reserve in 2 piles
//    */
//  @combinator object StalactitesStructure {
//    def apply(sol: Solitaire, t: Tableau, r: Reserve, st:Stock, f:Foundation, base:Container): Solitaire = {
//      sol.setTableau(t)
//      sol.setReserve(r)
//      sol.setFoundation(f)
//      sol.setStock(st)
//      sol.setContainer("Base", base)     // specialized container to hold CardViews
//
//      sol
//    }
//
//    // When given an empty Solitaire type, give back the semantic structure of Stalactites.
//    val semanticType: Type =
//      ('Solitaire ('Reserve ('None) :&: 'Tableau ('None) :&: 'Foundation('None)) :&: 'Solitaire ('Layout ('None)) :&: 'Solitaire ('Rules('None))) =>:
//        'Tableau ('Valid :&: 'Eight :&: 'Column) =>:
//        'Reserve ('Valid :&: 'Two :&: 'ReservePile) =>:
//        'Stock ('Valid :&: 'One :&: 'Deck) =>:
//        'Foundation ('Valid :&: 'Four :&: 'Pile) =>:
//        'Base ('Valid :&: 'Four :&: 'Card) =>:
//        'Solitaire ('Structure ('Stalactites))
//  }
//
//  /**
//    * Given a Stalactites Structure, its Rules, and its Layout,
//    * return the Stalactites variation.
//    */
//  @combinator object Construction {
//    def apply(sol: Solitaire, rules: Rules, layout: Layout): Solitaire = {
//      sol.setLayout(layout)
//      sol.setRules(rules)
//      sol
//    }
//
//    val semanticType: Type =
//      'Solitaire ('Structure ('Stalactites)) =>:
//        'Rules('Stalactites) =>:
//        'Layout('Valid :&: 'StalactitesLayout) =>:
//        'Variation('Stalactites)
//  }
//
//  /** Generate Reserve, Tableau, Foundation */
//
//  /** Manually generate the Aces Foundation, because it's split between Aces and Kings.
//    * The default NPileFoundation generates new Pile(), but we need AcesUpPile(), a subclass.
//    */
//  @combinator object StalactitesReserve {
//    def apply(): Reserve = {
//      val t = new Reserve()
//
//      for (_ <- 1 to 2)
//        t.add(new ReservePile())
//
//      t
//    }
//    val semanticType: Type = 'Reserve ('Valid :&: 'Two :&: 'ReservePile)
//  }
//
//  /**
//    * First four cards are dealt out and positioned into these containers.
//    */
//  @combinator object StalactitesBase {
//    def apply(): Container = {
//      val t = new Container()
//
//      for (_ <- 1 to 4)
//        t.add(new Card())
//
//      t
//    }
//    val semanticType: Type = 'Base ('Valid :&: 'Four :&: 'Card)
//  }

  /**
    * Wikipedia description:
    *
    * The player deals four cards from the deck. These four cards form the (base for ) foundations.
    * These are placed in four CardView elements which are the "Base"
The rest of the cards are dealt into eight columns of six cards each on the tableau. These cards can only be built up on the foundations regardless of suit and they cannot be built on each other.
The initial layout of a game of Stalactites.

Before the game starts, the player can decide on how the foundations should be built. Building can be either in ones (A-2-3-4-5-6-7-8-9-10-J-Q-K) or in twos (A-3-5-7-9-J-K-2-4-6-8-10-Q). Once the player makes up his mind, he begins building on the foundations from the cards on the tableau. The foundations are built, as already mentioned, up regardless of suit, and it goes round the corner, building from King to Ace (if building by ones) or from Queen to Ace (if building by twos) if necessary. The foundation cards turned sideways, though not necessarily be done, is a reminder of the last card's rank on each foundation.

The cards in the tableau should be placed in the foundations according to the building method the player decides to use. But when there are cards that cannot (or does not want to) be moved to the foundations, certain cards can be placed on a reserve. Any card can be placed on the reserve. But once a card is placed on the reserve, it must be built on a foundation; it should never return to the tableau. Furthermore, the reserve can only hold two cards.

The game is won when all cards are built onto the foundations, each having 13 cards. The four starting cards in the foundations don't have to be of the same rank; so results vary with each won game.

    */

//  @combinator object StalactitesRules {
//    def apply(solitaire: Solitaire): Rules = {
//      val rules = new Rules()
//
//      // Get Domain objects from Solitaire Object.
////      val tableau = solitaire.getTableau
////      val reserve = solitaire.getReserve
////      val acesFoundation = solitaire.getFoundation
////      val kingsFoundation = solitaire.getContainer("KingsDownFoundation")
//
////
////
////      /* Contraint saying if the moving card has the same suit as the top-facing destination card. */
////      // Note that these are real fields.
////      val sameSuit = new SameSuit("movingCard", "destination.peek()")
////
////      /* A Card can move to the Aces Foundation if the moving card is
////       * one rank higher and has the same suit.
////       */
////      val moveToAcesCondition = new IfConstraint(
////        new AndConstraint(
////          new NextRank("movingCard", "destination.peek()"),
////          sameSuit))
////
////      /* A Card can move to the Kings Foundation if the moving card is
////       * one rank lower and has the same suit.
////       */
////      val moveToKingsCondition = new IfConstraint(
////        new AndConstraint(
////          new NextRank("destination.peek()", "movingCard"),
////          sameSuit))
////
////      /* Add Rules */
////
////      // Note that the string argument becomes a real classname.
////      rules.addDragMove(
////        new SingleCardMove("TableauToFoundation",
////          tableau, acesFoundation, moveToAcesCondition
////        )
////      )
////
////      rules.addDragMove(
////        new SingleCardMove("ReserveToFoundation",
////          reserve, acesFoundation, moveToAcesCondition
////        )
////      )
////
////      rules.addDragMove(
////        new SingleCardMove("TableauToKingsFoundation",
////          tableau, kingsFoundation, moveToKingsCondition
////        )
////      )
////
////      rules.addDragMove(
////        new SingleCardMove("ReserveToKingsFoundation",
////          reserve, kingsFoundation, moveToKingsCondition
////        )
////      )
////
////      rules.addDragMove(
////        new SingleCardMove("ReserveToTableau",
////          reserve, tableau, new IfConstraint(new IsEmpty("destination")))
////      )
////
////      rules.addDragMove(
////        new SingleCardMove("TableauToTableau", tableau, tableau, new IfConstraint(new Falsehood))
////      )
//
//      rules
//    }
//    val semanticType:Type = 'Solitaire('Structure('Stalactites)) =>: 'Rules('Stalactites)
//  }
//
//  /**
//    * Defines Stalactites's placement of the Reserve, Foundations, and Tableau.
//    */
//  @combinator object StalactitesLayout {
//    def apply(): Layout = {
//      val lay = new Layout()
//
//      val card_width = 73
//      val card_height = 97
//
//      // signature is (name, x, y, width, height)
//      lay.add(Layout.Reserve,        15,   20,  2 * card_width + 40, card_height)
//      lay.add("Base",                200,  20,  4 * card_width + 80, card_height)
//      lay.add(Layout.Foundation,     200, 120,  4 * card_width + 80, card_height)
//      lay.add(Layout.Tableau,        15,  260,  8 * card_width + 80, 13 * card_height)
//
//      lay
//    }
//    val semanticType: Type = 'Layout ('Valid :&: 'StalactitesLayout)
//  }
}
