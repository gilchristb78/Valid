package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object gypsy extends variationPoints {
  val gypsy:Solitaire = {
    Solitaire(name = "Gypsy",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, buildFoundation, foundationToTableauMove, flipMove, deckDealMove),
      logic = BoardState(Map(Foundation -> 104)),
      customizedSetup = Seq(TableauToEmptyTableau, TableauToNextTableau, TableauToEmptyFoundation, TableauToNextFoundation,
        TableauToTableauMultipleCards, TableauToEmptyTableauMultipleCards)
    )
  }
}