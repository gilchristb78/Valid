package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object golf extends variationPoints{
  val golf:Solitaire = {

    Solitaire( name="Golf",
      structure = map,
      layout = golfLayout(),
      deal = getDeal(),
      specializedElements = Seq(WastePile),
      moves = Seq(tableauToWasteMove,deckDealMove, getTableauToTableauMove()),
      logic = BoardState(Map(Waste -> 52))
    )
  }
}
