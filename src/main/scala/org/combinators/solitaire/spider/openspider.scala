package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.spider.variationPoints


package object openspider extends variationPoints {

  //a normal spider deal, but without facedown cards
  override def getDeal: Seq[DealStep] = {
    var colNum:Int = 0
    var dealSeq:Seq[DealStep] = Seq()// doesn't like me declaring it without initializing
    for (colNum <- 0 to 3) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 6))
    }
    for (colNum <- 4 to 9) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 5))
    }
    dealSeq
  }

  val openspider:Solitaire = {
    Solitaire(name = "OpenSpider",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove, flipMove),
      logic = BoardState(Map(Foundation -> 104)),
      solvable = false
    )
  }
}