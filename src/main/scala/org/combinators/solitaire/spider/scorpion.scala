package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.spider.variationPoints


package object scorpion extends variationPoints {

  //TODO behaves strangely when overriding numTableau/Foundation, hardcoded in maps for now
  //override val numTableau:Int = 8
  //override val numFoundation:Int = 4
  //override val numStock:Int = 1

  override val map:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> horizontalPlacement(15, 200, 7, 13*card_height),
    StockContainer -> horizontalPlacement(15, 20, 1, card_height),
    Foundation -> horizontalPlacement(293, 20, 4, card_height)
  )


  override val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](7)(BuildablePile),
    Foundation -> Seq.fill[Element](4)(Pile),
    StockContainer -> Seq(Stock(1))
  )

  override def getDeal: Seq[DealStep] = {
    var colNum: Int = 0
    var dealSeq: Seq[DealStep] = Seq()
    for (colNum <- 0 to 3) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = 3))
    }
    colNum = 0
    for (colNum <- 4 to 6) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 3))
    }

    colNum = 0
    for (colNum <- 0 to 6) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 4))
    }

    dealSeq
  }

  override def buildOnTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomMoving = BottomCardOf(cards)
    val isEmpty = IsEmpty(Destination)
    val sameSuit = SameSuit(topDestination, bottomMoving)

    IfConstraint(IsKing(bottomMoving), isEmpty, AndConstraint(sameSuit, NextRank(topDestination, bottomMoving, true)))
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
      logic = BoardState(Map(Foundation -> 52)),
      solvable = false
    )
  }
}