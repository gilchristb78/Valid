package org.combinators.solitaire.spider

import org.combinators.solitaire.domain._

package object gigantic extends closedSpiderVariationPoints {

  override def numTableau: Int = 15
  override def numFoundation: Int = 16
  override def numStock: Int = 4

  override def getDeal: Seq[DealStep] = {
    var colNum: Int = 0
    var dealSeq: Seq[DealStep] = Seq()
    for (colNum <- 0 to 6) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = 5))
    }
    colNum = 7
    for (colNum <- 7 to 14) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = 4))
    }

    //each pile gets a face up card
    colNum = 0
    for (colNum <- 0 to 14) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload())
    }

    dealSeq
  }

  val gigantic:Solitaire = {
    Solitaire(name = "Gigantic",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove, flipMove),
      logic = BoardState(Map(Foundation -> 208))
    )
  }
}