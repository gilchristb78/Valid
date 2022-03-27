package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.spider.variationPoints


package object mrsmop extends variationPoints {

  override def numTableau(): Int ={
    13
  }

  override def numFoundation(): Int ={
    8
  }

  override def numStock(): Int ={
    2
  }

  //Ms. Mop uses a very straightforward deal, thirteen columns of 8 face-up cards
  override def getDeal: Seq[DealStep] = {
    var colNum: Int = 0
    var dealSeq: Seq[DealStep] = Seq()
    colNum = 0
    for (colNum <- 0 to 12) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 8))
    }

    dealSeq
  }

  val mrsmop:Solitaire = {
    Solitaire(name = "MrsMop",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove),
      logic = BoardState(Map(Foundation -> 104))
    )
  }
}