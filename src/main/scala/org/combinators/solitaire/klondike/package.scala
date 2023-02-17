package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object klondike extends variationPoints {

  val klondike:Solitaire = {
    Solitaire( name="Klondike",
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
