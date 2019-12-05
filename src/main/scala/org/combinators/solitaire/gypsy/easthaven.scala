package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.gypsy.variationPoints


package object easthaven extends variationPoints {
  override def getNumStock():Int = 1
  override def getNumTableau():Int = 7
  override def getNumFoundation():Int = 4

  val easthaven:Solitaire = {
    Solitaire(name = "EastHaven",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, buildFoundation, flipMove, foundationToTableauMove, deckDealMove),
      logic = BoardState(Map(Foundation -> 52)),
      solvable = false,
      testSetup = Seq(),
    )
  }
}