package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.spider.closedVariationPoints


package object baby extends closedVariationPoints {

  override def numTableau(): Int ={
    8
  }

  override def numFoundation(): Int ={
    4
  }

  override def numStock(): Int ={
    1
  }

  override def getDeal: Seq[DealStep] = {
    var colNum: Int = 1
    var dealSeq: Seq[DealStep] = Seq() // doesn't like me declaring it without initializing
    // Klondike deal - the ith pile gets i face down cards
    for (colNum <- 1 to 7) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = colNum))
    }
    //each pile gets a face up card
    colNum = 0
    for (colNum <- 0 to 7) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 1))
    }

    dealSeq
  }

  override def buildOnTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomMoving = BottomCardOf(cards)
    val isEmpty = IsEmpty(Destination)
    val descend = Descending(cards)

    AndConstraint( descend, OrConstraint(isEmpty, NextRank(topDestination, bottomMoving, true)) )
  }

  val baby:Solitaire = {
    Solitaire(name = "Baby",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove, flipMove),
      logic = BoardState(Map(Foundation -> 52)),
      solvable = false,
      testSetup = Seq(),
    )
  }
}