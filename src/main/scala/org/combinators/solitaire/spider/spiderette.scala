package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object spiderette {

  case class AllSameSuit(movingCards: MoveInformation) extends Constraint

  val numTableau:Int = 10
  val numFoundation:Int = 8
  val map:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](numTableau)(BuildablePile),
    Foundation -> Seq.fill[Element](numFoundation)(Pile),
    StockContainer -> Seq(Stock(1)) //apparently this will break things further down the road
  )
  var colNum:Int = 1
  var dealSeq:Seq[DealStep] = Seq()// doesn't like me declaring it without initializing
  // Klondike deal - the ith pile gets i face down cards
  for (colNum <- 1 to 7) {
    //TODO change these to face down when FlipCardMove exists
    dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = colNum))
    //dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = colNum))
  }
  //each pile gets a face up card
  colNum = 0
  for (colNum <- 0 to 7) {
    dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 1))
  }


  val bottomMoving = BottomCardOf(MovingCards)
  val topMoving = TopCardOf(MovingCards)
  val topDestination = TopCardOf(Destination)
  val isEmpty = IsEmpty(Destination)
  val descend = Descending(MovingCards)
  //new
  val suit = AllSameSuit(MovingCards)
  //new
  val and_t = AndConstraint(descend, suit)

  val or = OrConstraint(isEmpty, NextRank(topDestination,
    bottomMoving))

  val tableauToTableau:Move = MultipleCardsMove ("MoveColumn", Drag,
    //source=(Tableau, Truth), target=Some((Tableau, AndConstraint(descend, or))))
    source=(Tableau, Truth), target=Some((Tableau, AndConstraint(and_t, or))))

  val f_and = AndConstraint(descend, AndConstraint(IsAce(topMoving), IsKing(bottomMoving)))

  val buildFoundation:Move = MultipleCardsMove("BuildFoundation", Drag,
    source=(Tableau, Truth), target=Some((Foundation, AndConstraint(f_and, isEmpty))))

  //TODO Add the flip move.

  // Deal card from deck
  val deckDealMove:Move = DealDeckMove("DealDeck", 1,
    source=(StockContainer, Truth), target=Some((Tableau, Truth)))

  val spider:Solitaire = {

    Solitaire( name="Spider",
      structure = map,
      solvable = false,

      layout = Layout(Map(
        Tableau -> horizontalPlacement(15, 200, numTableau, 13*card_height),
        StockContainer -> horizontalPlacement(15, 20, 1, card_height),
        Foundation -> horizontalPlacement(293, 20, numFoundation, card_height),
        Waste -> horizontalPlacement(95, 20, 1, card_height)
      )),

      deal = dealSeq,

      specializedElements = Seq.empty,

      /** All rules here. */
      moves = Seq(tableauToTableau
        ,deckDealMove
        ,buildFoundation),

      // fix winning logic
      logic = BoardState(Map(Foundation -> 52))
    )
  }
}
