package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.gypsy.variationPoints


package object nomad extends variationPoints {
  case object FreeCell extends Element(true)

  override val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](getNumTableau())(BuildablePile),
    Foundation -> Seq.fill[Element](getNumFoundation())(Pile),
    StockContainer -> Seq(Stock(getNumStock())),
    Reserve -> Seq(FreeCell)
  )

  override val map:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> horizontalPlacement(100, 200, 8, 13*card_height),
    StockContainer -> horizontalPlacement(15, 20, 1, card_height),
    Foundation -> horizontalPlacement(100, 20, 8, card_height),
    Reserve -> horizontalPlacement(15, 200, 1, card_height)
  )

  override def getDeal(): Seq[DealStep] = {
    Seq(DealStep(ContainerTarget(Tableau), Payload(faceUp=true, numCards=4)))
  }

  val freeCellConstraint:Constraint = AndConstraint(
    IsEmpty(Destination),
    IsSingle(MovingCards)
  )

  val fcToFoundationConstraint:Constraint = OrConstraint(
    AndConstraint(IsEmpty(Destination), IsAce(MovingCard)),
    AndConstraint(NextRank(MovingCard, TopCardOf(Destination)),
      SameSuit(MovingCard, TopCardOf(Destination)))
  )

  val moveToFreeCell:Move = MultipleCardsMove("TableauToFreeCell", Drag,
    source=(Tableau, Truth), target=Some((Reserve, freeCellConstraint)))

  val moveFoundationToFreeCell:Move = SingleCardMove("MoveFoundationToFreeCell", Drag,
    source=(Foundation,Truth), target=Some((Reserve, IsEmpty(Destination))))

  val moveFreeCellToFoundation:Move = SingleCardMove("MoveFreeCellToFoundation", Drag,
    source=(Reserve,Truth), target=Some((Foundation, fcToFoundationConstraint)))

  val nomad:Solitaire = {
    Solitaire(name = "Nomad",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq(FreeCell),
      moves = Seq(tableauToTableauMove, buildFoundation, flipMove, foundationToTableauMove, deckDealMove, moveToFreeCell, moveFoundationToFreeCell, moveFreeCellToFoundation),
      logic = BoardState(Map(Foundation -> 104)),
      solvable = false,
      testSetup = Seq(),
    )
  }
}