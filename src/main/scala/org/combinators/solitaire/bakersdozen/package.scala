package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object bakersdozen extends variationPoints {

  val bakersdozen:Solitaire = {
    Solitaire(name = "Bakersdozen",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove),  //deckDealMove before flipmove and in varpoints. End with flipmove
      logic = BoardState(Map(Foundation -> 52)),
      solvable = false,
      testSetup = Seq()
    )
  }
}