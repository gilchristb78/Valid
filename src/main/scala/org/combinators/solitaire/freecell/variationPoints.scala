package org.combinators.solitaire.freecell

import org.combinators.solitaire.domain._

trait variationPoints  {

   val numDecks:Int = 1

   val structureMap:Map[ContainerType,Seq[Element]] = Map(
    StockContainer -> Seq.fill[Element](1)(Stock(numDecks)),   // a deck must be present even though not visible
    Tableau -> Seq.fill[Element](8)(Column),
    Foundation -> Seq.fill[Element](4)(Pile),
    Reserve -> Seq.fill[Element](4)(FreeCellPile),
  )

  val layoutMap:Map[ContainerType, Seq[Widget]] = Map (
    Reserve -> horizontalPlacement(10, 10, 4, card_height),
    Foundation -> horizontalPlacement(400, 10, 4, card_height),
    Tableau -> horizontalPlacement(10, 200, num = 8, height = card_height*8),  // estimate
  )

  val tt_move:Constraint = IfConstraint(IsEmpty(Destination), AndConstraint(buildOnEmptyTableauMultiple(MovingCards), Descending(MovingCards), AlternatingColors(MovingCards)), buildOnTableau(MovingCards))
  val tt_move_one_card:Constraint = IfConstraint(IsEmpty(Destination), buildOnEmptyTableauSingle(MovingCard), buildOnTableauOneCard(MovingCard))

  val tf_move:Constraint = IfConstraint(IsEmpty(Destination), buildOnEmptyFoundation(MovingCard), buildOnFoundation(MovingCard))
  val tf_move_multiple:Constraint = AndConstraint(IsSingle(MovingCards),
    IfConstraint(IsEmpty(Destination), buildOnEmptyFoundationMultiple(MovingCards), buildOnFoundationMultiple(MovingCards)))

  val tableauToTableauMove:Move = MultipleCardsMove("MoveColumn", Drag,    // SingleCardMove  was "MoveCard"
    source=(Tableau,Truth), target=Some((Tableau, AndConstraint(tt_move, IsSufficientFree (MovingCards, Source, Destination)))))

  val tableauToFoundationMove:Move = MultipleCardsMove("MoveCardFoundation", Drag,
    source=(Tableau,Truth), target=Some((Foundation, tf_move_multiple)))
  val fromTableauToReserve:Move = MultipleCardsMove("TableauToReserve", Drag,
    source=(Tableau,Truth), target=Some((Reserve, AndConstraint(IsSingle(MovingCards), IsEmpty(Destination)))))

  val fromReserveToReserve:Move = SingleCardMove("ReserveToReserve", Drag,
    source=(Reserve,Truth), target=Some((Reserve, IsEmpty(Destination))))

  val fromReserveToTableau:Move = SingleCardMove("ReserveToTableau", Drag,
    source=(Reserve,Truth), target=Some((Tableau, tt_move_one_card)))

  val fromReserveToFoundation:Move = SingleCardMove("ReserveToFoundation", Drag,
    source=(Reserve,Truth), target=Some((Foundation, tf_move)))

  def buildOnTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomCards = BottomCardOf(cards)
    AndConstraint(NextRank(topDestination, bottomCards), OppositeColor(bottomCards, topDestination))   // was all card and MovingCard.type
  }

  def buildOnTableauOneCard(card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    AndConstraint(NextRank(topDestination, card), OppositeColor(card, topDestination))
  }

  def buildOnFoundation(card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    AndConstraint(NextRank(card, topDestination),  SameSuit(card, topDestination))
  }
  def buildOnFoundationMultiple(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    AndConstraint(NextRank(BottomCardOf(cards), topDestination),  SameSuit(BottomCardOf(cards), topDestination))
  }
  def buildOnEmptyFoundation(card: MovingCard.type): Constraint = {
    IsAce(card)
  }
  def buildOnEmptyFoundationMultiple(cards: MovingCards.type): Constraint = {
    IsAce(BottomCardOf(cards))
  }

  // rules for moving to an empty tableau.
  def buildOnEmptyTableauSingle(card:MovingCard.type) : Constraint = {
    Truth
  }
// IsKing(BottomCardOf(cards))
  def buildOnEmptyTableauMultiple(cards: MovingCards.type) : Constraint = {
    Truth
  }
}