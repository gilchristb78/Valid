package org.combinators.solitaire

import org.combinators.solitaire.domain._


package object spider extends variationPoints {
  val spider:Solitaire = {
    Solitaire(name = "Spider",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove),
      logic = BoardState(Map(Foundation -> 104)),
      solvable = false
    )
  }
}