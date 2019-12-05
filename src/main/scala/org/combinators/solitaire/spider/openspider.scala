package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.spider.variationPoints


package object openspider extends variationPoints {

  val openspider:Solitaire = {
    Solitaire(name = "OpenSpider",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      //moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove, flipMove),
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove),
      logic = BoardState(Map(Foundation -> 104)),
      solvable = false,
      testSetup = Seq(),
    )
  }
}