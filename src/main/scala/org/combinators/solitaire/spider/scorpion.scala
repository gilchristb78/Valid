package org.combinators.solitaire.spider

import org.combinators.solitaire.domain._

package object scorpion extends closedSpiderVariationPoints {

  override def numTableau: Int = 7
  override def numFoundation: Int = 4
  override def numStock: Int = 1

  override def getDeal: Seq[DealStep] = {
    var colNum: Int = 0
    var dealSeq: Seq[DealStep] = Seq()
    for (colNum <- 0 to 3) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = 3))
    }
    colNum = 0
    for (colNum <- 4 to 6) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards = 3))
    }

    colNum = 0
    for (colNum <- 0 to 6) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards = 4))
    }

    dealSeq
  }

  override def buildOnTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomMoving = BottomCardOf(cards)
    val isEmpty = IsEmpty(Destination)
    val sameSuit = SameSuit(topDestination, bottomMoving)

    IfConstraint(IsKing(bottomMoving), isEmpty, AndConstraint(sameSuit, NextRank(topDestination, bottomMoving, wrapAround=true)))
  }

  //TODO a way to deal out just these 3 last cards?
  //override val deckDealMove:Move = DealDeckMove("DealDeck", 1,
    //source=(StockContainer, NotConstraint(IsEmpty(Source))), target=Some((Tableau, Truth)))


  val scorpion:Solitaire = {
    Solitaire(name = "Scorpion",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove, flipMove),
      logic = BoardState(Map(Foundation -> 52))
    )
  }
}