package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.spider.variationPoints


package object curdsandwhey extends variationPoints {

  override def numTableau(): Int ={
    13
  }

  override def numFoundation(): Int ={
    4
  }

  override def numStock(): Int ={
    1
  }
  override val map:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> horizontalPlacement(15, 200, numTableau(), 13*card_height),
    Foundation -> horizontalPlacement(293, 20, numFoundation(), card_height)
  )


  override val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](numTableau())(BuildablePile),
    Foundation -> Seq.fill[Element](numFoundation())(Pile),
    StockContainer -> Seq(Stock(1))
  )

  override def getDeal: Seq[DealStep] = {
    var colNum: Int = 0
    var dealSeq: Seq[DealStep] = Seq()
    for (colNum <- 0 to 12) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 4))
    }
    dealSeq
  }

  // curds and whey allows us to build down like suit, or to stack cards of the same rank
  // we can move a column if it's built down as a single suit, or if it is all cards of the same rank
  // BUT, we can't if it fulfills both conditions (xor)
  override def buildOnTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomMoving = BottomCardOf(cards)
    val isEmpty = IsEmpty(Destination)
    val suit = AllSameSuit(cards)
    val rank = AllSameRank(cards)
    val suitBuild = AndConstraint(NextRank(topDestination, bottomMoving, true), SameSuit(topDestination, bottomMoving))

    val sr_xor = OrConstraint(AndConstraint(suit, NotConstraint(rank)), AndConstraint(NotConstraint(suit), rank))

    IfConstraint(isEmpty, IsKing(bottomMoving),
      AndConstraint(sr_xor,
        OrConstraint(suitBuild, SameRank(topDestination, bottomMoving))))
  }

  val curdsandwhey:Solitaire = {
    Solitaire(name = "CurdsAndWhey",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      //moves = Seq(tableauToTableauMove, tableauToFoundationMove, flipMove),
      moves = Seq(tableauToTableauMove, tableauToFoundationMove),
      logic = BoardState(Map(Foundation -> 52)),
      solvable = false,
      testSetup = Seq(),
    )
  }
}