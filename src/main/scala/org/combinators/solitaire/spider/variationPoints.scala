package org.combinators.solitaire.spider

import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.generic
import org.combinators.solitaire.domain._
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._
import org.combinators.templating.twirl.Java
/** Defines Spider's variation points
  */
trait variationPoints {

  case class AllSameSuit(movingCards: MoveInformation) extends Constraint

  //val numTableau:Int = 10
  //val numFoundation:Int = 8
  val numStock:Int = 2

  val map:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> horizontalPlacement(15, 200, 10, 13*card_height),
    StockContainer -> horizontalPlacement(15, 20, 1, card_height),
    Foundation -> horizontalPlacement(293, 20, 8, card_height)
  )


  val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](10)(BuildablePile),
    Foundation -> Seq.fill[Element](8)(Pile),
    StockContainer -> Seq(Stock(numStock))
  )

  def getDeal: Seq[DealStep] = {
    var colNum:Int = 0
    var dealSeq:Seq[DealStep] = Seq()// doesn't like me declaring it without initializing
    //first four piles get 5 face down cards
    for (colNum <- 0 to 3) {
      //TODO change these to face down when FlipCardMove exists
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 5))
      //dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = 5))
    }
    //the rest get 4 face down cards
    for (colNum <- 4 to 9) {
      //TODO change these to face down when FlipCardMove exists
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 4))
      //dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = 4))
    }
    //each pile gets a face up card
    colNum = 0
    for (colNum <- 0 to 9) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 1))
    }
    dealSeq
  }

  //DO THESE NEED INPUT AT ALL?
  //can probably organize this a bit better
  def buildOnTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomMoving = BottomCardOf(cards)
    val isEmpty = IsEmpty(Destination)
    val descend = Descending(cards)
    val suit = AllSameSuit(cards)

    AndConstraint( AndConstraint(descend, suit), OrConstraint(isEmpty, NextRank(topDestination, bottomMoving)) )
  }

  //can probably organize this a bit better
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

  val deckDealMove:Move = DealDeckMove("DealDeck", 1,
    source=(StockContainer, NotConstraint(IsEmpty(Source))), target=Some((Tableau, Truth)))

}