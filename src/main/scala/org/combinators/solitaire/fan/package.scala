package org.combinators.solitaire

import org.combinators.solitaire.domain._


package object fan extends variationPoints {
  val fan:Solitaire = {
    Solitaire(name = "Fan",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove),
      logic = BoardState(Map(Tableau -> 0, Foundation -> 52)),
      solvable = true,
      testSetup = Seq(),
    )
  }
}
