package org.combinators.solitaire

import org.combinators.solitaire.domain._


package object simplesimon extends variationPoints {

  val simplesimon:Solitaire = {
    Solitaire(name = "Simplesimon",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove),
      logic = BoardState(Map(Foundation -> 52))
    )
  }
}