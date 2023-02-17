package org.combinators.solitaire.spider

import org.combinators.solitaire.domain._


package object openspider extends spiderVariationPoints {

  val openspider:Solitaire = {
    Solitaire(name = "OpenSpider",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      //moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove, flipMove),
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove),
      logic = BoardState(Map(Foundation -> 104))
    )
  }
}