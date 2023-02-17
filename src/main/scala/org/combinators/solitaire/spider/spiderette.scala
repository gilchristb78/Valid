package org.combinators.solitaire.spider

import org.combinators.solitaire.domain._

package object spiderette extends closedSpiderVariationPoints {

  override def numTableau: Int = 8
  override def numFoundation: Int = 4
  override def numStock: Int = 1

  override def getDeal: Seq[DealStep] = {
    var colNum: Int = 1
    var dealSeq: Seq[DealStep] = Seq() // doesn't like me declaring it without initializing

    for (colNum <- 1 to 7) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = colNum))
    }
    //each pile gets a face up card
    colNum = 0
    for (colNum <- 0 to 7) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload())
    }

    dealSeq
  }

  val spiderette:Solitaire = {
    Solitaire(name = "Spiderette",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove, flipMove),
      logic = BoardState(Map(Foundation -> 52))
    )
  }
}