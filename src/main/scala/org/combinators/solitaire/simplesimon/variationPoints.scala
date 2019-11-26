package org.combinators.solitaire.simplesimon

import org.combinators.solitaire.domain._
import com.github.javaparser.ast.body.{BodyDeclaration, MethodDeclaration}
import org.combinators.templating.twirl.Java
//Simple Simon VariationPoints
trait variationPoints {
  case class AllSameSuit(movingCards: MoveInformation) extends Constraint
  case class AllSameRank(movingCards: MoveInformation) extends Constraint
  case class EmptyPiles(src:MoveInformation) extends Constraint

  def numTableau(): Int ={
    10
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
    Tableau -> horizontalPlacement(15, 200, numTableau(), 13*card_height),
    StockContainer -> horizontalPlacement(15, 20, numStock(), card_height),
    Foundation -> horizontalPlacement(293, 20, numFoundation(), card_height)
  )

  def getDeal: Seq[DealStep] = {
    var colNum:Int = 0
    var cardNumCounter:Int = 8
    var dealSeq:Seq[DealStep] = Seq()// doesn't like me declaring it without initializing

    for(colNum <- 0 to 1){
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum),
        Payload(faceUp = true, numCards = 8))
    }
    for(colNum <- 2 to 9){
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum),
        Payload(faceUp = true, numCards = cardNumCounter))
      cardNumCounter -= 1
    }
    dealSeq
  }

  def buildOnTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomMoving = BottomCardOf(cards)
    val isEmpty = IsEmpty(Destination)
    val descend = Descending(cards)
    val suit = AllSameSuit(cards)
    AndConstraint( AndConstraint(descend, suit), OrConstraint(isEmpty,
      NextRank(topDestination, bottomMoving, true)) )
  }

  def buildOnFoundation(cards: MovingCards.type): Constraint = {
    val topMoving = TopCardOf(cards)
    val bottomMoving = BottomCardOf(cards)
    val descend = Descending(cards)
    val suit = AllSameSuit(cards)
    AndConstraint( AndConstraint(descend, suit),
      AndConstraint(IsAce(topMoving), IsKing(bottomMoving)) )
  }

  val tableauToTableauMove:Move = MultipleCardsMove("MoveColumn", Drag,
    source=(Tableau,Truth), target=Some((Tableau, buildOnTableau(MovingCards))))

  val tableauToFoundationMove:Move = MultipleCardsMove("MoveCardFoundation", Drag,
    source=(Tableau,Truth), target=Some((Foundation, AndConstraint(
      IsEmpty(Destination), buildOnFoundation(MovingCards)))))

  val allowed = AndConstraint(NotConstraint(IsEmpty(Source)),
    NotConstraint(IsFaceUp(TopCardOf(Source))))
//  val flipMove:Move = FlipCardMove("FlipCard", Press, source = (Tableau, allowed))

}

