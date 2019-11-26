package org.combinators.solitaire

import org.combinators.solitaire.domain._

/**
  * This is the model to be used by both Python and Java extensions. So we have to be careful not
  * to embed any specific logic here.
  */
package object idiot {

  case class HigherRankSameSuit(on: MoveInformation) extends Constraint

  val idiot:Solitaire = {
    val numTableau:Int = 4
    val map:Map[ContainerType,Seq[Element]] = Map(
      Tableau -> Seq.fill[Element](numTableau)(Column),
      StockContainer -> Seq(Stock(1))
    )

    val isEmpty = IsEmpty(Destination)
    val tableauToTableauMove = SingleCardMove("MoveCard", Drag,
           source=(Tableau,Truth), target=Some((Tableau, isEmpty)))

    // this special method is added by gameDomain to be accessible here.
    val sameSuitHigherRankVisible = HigherRankSameSuit(Source)

    val and = AndConstraint(NotConstraint(IsEmpty(Source)), sameSuitHigherRankVisible)

    val removeCardFromTableau = RemoveSingleCardMove("RemoveCard", Click,
      source=(Tableau, and), target=None)

    // Remove a card from the tableau? This can be optimized by a click
    // do I allow another Rule? Or reuse existing one?
    // Not sure how to deal with MOVE with a single PRESS
    // That is, this will not be the head part of a drag operation.

    // deal four cards from Stock
    val deck_move = NotConstraint(IsEmpty(Source))
    val deckDeal =  DealDeckMove("DealDeck",
      numToDeal = 1,
      source=(StockContainer, deck_move),
      target=Some((Tableau, Truth))
    )

    Solitaire( name="Idiot",

      structure = map,

      layout=stockTableauColumnLayout(numTableau),

      deal = Seq(DealStep(ContainerTarget(Tableau))),

      /** from element can infer ks.ViewWidget as well as Base Element. */
      specializedElements = Seq.empty,

      /** All rules here. */
      moves = Seq(tableauToTableauMove,removeCardFromTableau, deckDeal),

      // fix winning logic
      logic = BoardState(Map(Tableau -> 0)),
      testSetup = Seq()

    )
  }
}
