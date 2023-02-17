package org.combinators.solitaire.spider

import org.combinators.solitaire.domain._

/** Defines Spider's variation points
  */
trait spiderVariationPoints {

  case class AllSameSuit(movingCards: MoveInformation) extends Constraint
  case class AllSameRank(movingCards: MoveInformation) extends Constraint
  case class EmptyPiles(src:MoveInformation) extends Constraint

  def numTableau: Int = 10
  def numFoundation: Int = 8
  def numStock: Int = 2

  val map:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> horizontalPlacement(15, 200, numTableau, 13*card_height),
    StockContainer -> horizontalPlacement(15, 20, 1, card_height),
    Foundation -> horizontalPlacement(293, 20, numFoundation, card_height)
  )

  val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](numTableau)(BuildablePile),
    Foundation -> Seq.fill[Element](numFoundation)(Pile),
    StockContainer -> Seq(Stock(numStock))
  )

  def getDeal: Seq[DealStep] = {
    var colNum:Int = 0
    var dealSeq:Seq[DealStep] = Seq()// doesn't like me declaring it without initializing
    for (colNum <- 0 to 3) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards = 6))
    }
    for (colNum <- 4 to 9) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards = 5))
    }
    dealSeq
  }

  def buildOnTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomMoving = BottomCardOf(cards)
    val isEmpty = IsEmpty(Destination)
    val descend = Descending(cards)
    val suit = AllSameSuit(cards)

    AndConstraint( AndConstraint(descend, suit), OrConstraint(isEmpty, NextRank(topDestination, bottomMoving, wrapAround=true)) )
  }

  def buildOnFoundation(cards: MovingCards.type): Constraint = {
    val topMoving = TopCardOf(cards)
    val bottomMoving = BottomCardOf(cards)
    val descend = Descending(cards)
    val suit = AllSameSuit(cards)

    AndConstraint( AndConstraint(descend, suit), AndConstraint(IsAce(topMoving), IsKing(bottomMoving)) )
  }

  val tableauToTableauMove:Move = MultipleCardsMove("MoveColumn", Drag,
    source=(Tableau,Truth), target=Some((Tableau, buildOnTableau(MovingCards))))

  val tableauToFoundationMove:Move = MultipleCardsMove("MoveCardFoundation", Drag,
    source=(Tableau,Truth), target=Some((Foundation, AndConstraint( IsEmpty(Destination), buildOnFoundation(MovingCards)))))

  val deckCon = AndConstraint(NotConstraint(IsEmpty(Source)), NotConstraint(EmptyPiles(Tableau)))
  val deckDealMove:Move = DealDeckMove("DealDeck", 1,
    source=(StockContainer, deckCon), target=Some((Tableau, Truth)))
}