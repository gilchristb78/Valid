package org.combinators.solitaire.bakersdozen

import org.combinators.solitaire.domain._
//Simple Simon VariationPoints
trait variationPoints {
  case class AllSameSuit(movingCards: MoveInformation) extends Constraint
  case class AllSameRank(movingCards: MoveInformation) extends Constraint
  case class EmptyPiles(src:MoveInformation) extends Constraint

  def numTableau(): Int ={
    13
  }

  def numFoundation(): Int ={
    4
  }

  def numStock(): Int ={
    0
  }
  val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](numTableau())(Column),
    Foundation -> Seq.fill[Element](numFoundation())(Pile),
    StockContainer -> Seq(Stock(numStock()))
  )

  val map:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> horizontalPlacement(15, 200, 13, 13*card_height),
    //StockContainer -> horizontalPlacement(15, 20, 1, card_height),
    Foundation -> horizontalPlacement(293, 20, 4, card_height)
  )

  def getDeal: Seq[DealStep] = {
    var colNum:Int = 0
    var cardNumCounter:Int = 8
    var dealSeq:Seq[DealStep] = Seq()// doesn't like me declaring it without initializing

    for(colNum <- 0 to 12){
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum),
        Payload(faceUp = true, numCards = 4))
    }
    dealSeq
  }

  def buildOnTableau(card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    NextRank(topDestination, card)
  }

  def buildOnFoundation(card: MovingCard.type): Constraint = {
    val isEmpty = IsEmpty(Destination)
    val isAce = IsAce(card)
    val topDestination = TopCardOf(Destination)
      OrConstraint(AndConstraint(isEmpty, isAce), AndConstraint(SameSuit(topDestination, card)),NextRank(card, topDestination, true))
  }

  def buildOnEmptyFoundation(card: MovingCard.type): Constraint = {
    IsAce(card)
  }

  def buildOnEmptyTableau(card: MovingCard.type): Constraint = {
    IsKing(card)
  }

  val tf_move:Constraint = IfConstraint(IsEmpty(Destination), buildOnEmptyFoundation(MovingCard), buildOnFoundation(MovingCard))
  val tt_move:Constraint = IfConstraint(IsEmpty(Destination), buildOnEmptyTableau(MovingCard), buildOnTableau(MovingCard))



  val tableauToTableauMove:Move = SingleCardMove("MoveCard", Drag,
    source=(Tableau,Truth), target=Some((Tableau, tt_move)))
  val tableauToFoundationMove:Move = SingleCardMove("MoveCardFoundation ", Drag,
    source=(Tableau,Truth), target=Some(Foundation, tf_move))
  //  val deckDealMove:Move = DealDeckMove("DealDeck", 1,
  //    source=(StockContainer, NotConstraint(IsEmpty(Source))), target=Some((Tableau,
  //      Truth)))
  val allowed = AndConstraint(NotConstraint(IsEmpty(Source)),
    NotConstraint(IsFaceUp(TopCardOf(Source))))
  //  val flipMove:Move = FlipCardMove("FlipCard", Press, source = (Tableau, allowed))


}
