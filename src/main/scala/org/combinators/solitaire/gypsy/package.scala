package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.gypsy.variationPoints


package object gypsy extends variationPoints {
  val gypsy:Solitaire = {
    Solitaire(name = "Gypsy",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, buildFoundation, foundationToTableauMove, flipMove, deckDealMove),
      logic = BoardState(Map(Foundation -> 104)),
      solvable = false,
      testSetup = Seq()
    )
  }
}