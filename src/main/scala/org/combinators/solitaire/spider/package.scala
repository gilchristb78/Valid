package org.combinators.solitaire

import org.combinators.solitaire.domain._


package object spider extends closedVariationPoints {

  override def getDeal: Seq[DealStep] = {
    var colNum:Int = 0
    var dealSeq:Seq[DealStep] = Seq()// doesn't like me declaring it without initializing
    //first four piles get 5 face down cards
    for (colNum <- 0 to 3) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = 5))
    }
    //the rest get 4 face down cards
    for (colNum <- 4 to 9) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = 4))
    }
    //each pile gets a face up card
    colNum = 0
    for (colNum <- 0 to 9) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 1))
    }
    dealSeq
  }

  val spider:Solitaire = {
    Solitaire(name = "Spider",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove, flipMove),
      logic = BoardState(Map(Foundation -> 104)),
      solvable = false,
      testSetup = Seq()
    )
  }
}