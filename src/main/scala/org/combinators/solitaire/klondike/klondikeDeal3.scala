package org.combinators.solitaire.klondike

import org.combinators.solitaire.domain._

package object klondikeDeal3 extends variationPoints {

  override val numToDeal:Int = 3

  override val deckDeal:Move = DealDeckMove("DealDeck", numToDeal,   // have to reify here to get it with numDeal=3
    source=(StockContainer, DeckDeal), target=Some((Waste, Truth)))

  val klondike:Solitaire = {

    Solitaire( name="KlondikeDeal3",
      structure = klondikeMap,
      layout = Layout(klondikeLayout),
      deal = klondikeDeal,

      specializedElements = Seq(WastePile),

      moves = Seq(tableauToTableau, wasteToTableau, tableauToFoundation, wasteToFoundation, deckDeal, deckResetFromWaste, flipTableau),

      // fix winning logic
      logic = BoardState(Map(Foundation -> 52)),
      customizedSetup = Seq.empty
    )
  }
}
