package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.golf.variationPoints

package object robert extends variationPoints{

  override def getNumTableau(): Int = 1

  override def getDeal(): Seq[DealStep] = Seq.empty

  // reset deck by pulling together all cards from the piles.
  val deckReset:Move = ResetDeckMove("ResetDeck",
    source=(StockContainer,IsEmpty(Source)), target=Some((Tableau, Truth)))

  val dealToTableauMove:Move = DealDeckMove("DealToTableau", 1,
    source=(StockContainer, deck_move), target=Some((Tableau, Truth)))

  override val map:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](getNumTableau())(Pile),
    StockContainer -> Seq(Stock(getNumDecks())),
    Waste -> Seq.fill[Element](1)(WastePile)
  )

  override def golfLayout():Layout = {
    Layout(Map(
      StockContainer -> horizontalPlacement(15, 20, 1, card_height),
      Tableau -> horizontalPlacement(120, 20, getNumTableau(), card_height),
      Waste -> horizontalPlacement(15, 40 + card_height, 1, card_height)
    ))
  }

  val robert:Solitaire = {

    Solitaire( name="Robert",
      structure = map,
      layout = golfLayout(),
      deal = getDeal(),
      specializedElements = Seq(WastePile),
      moves = Seq(tableauToWasteMove,dealToTableauMove,deckReset),
      logic = BoardState(Map(Waste -> 52)),
      testSetup = Seq(),
    )
  }
}
