package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.gypsy.variationPoints

package object nomad extends variationPoints {
  case object FreeCell extends Element(true)

  override def buildOnTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomMoving = BottomCardOf(cards)
    val isEmpty = IsEmpty(Destination)
    val descend = Descending(cards)
    val suit = AlternatingColors(cards)
    OrConstraint(AndConstraint(isEmpty, descend, suit), AndConstraint(descend, suit, SameColor(topDestination, bottomMoving)))
  }

  override val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](getNumTableau)(BuildablePile),
    Foundation -> Seq.fill[Element](getNumFoundation)(Pile),
    StockContainer -> Seq(Stock(getNumStock)),
    Reserve -> Seq(FreeCell)
  )

  override val map:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> horizontalPlacement(100, 200, 8, 13*card_height),
    StockContainer -> horizontalPlacement(15, 20, 1, card_height),
    Foundation -> horizontalPlacement(100, 20, 8, card_height),
    Reserve -> horizontalPlacement(15, 200, 1, card_height)
  )

  override def getDeal: Seq[DealStep] = {
    Seq(DealStep(ContainerTarget(Tableau), Payload(numCards=4)))
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

  case object TableauToEmptyReserve extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Reserve, 2))

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),  //this should clear the 0th Reserve
      MovingCardStep(CardCreate(Clubs, Four)),                      // move Three to target
    )
  }

  case object ReserveToReserve extends Setup {
    val sourceElement = ElementInContainer(Reserve, 1)
    val targetElement = Some(ElementInContainer(Reserve, 2))

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      MovingCardStep(CardCreate(Clubs, Three)),        // move Three to target
    )
  }

  case object ReserveToEmptyTableau extends Setup {
    val sourceElement = ElementInContainer(Reserve, 1)
    val targetElement = Some(ElementInContainer(Tableau, 2))

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      MovingCardStep(CardCreate(Clubs, Six)),        // move Six to target, doesn't matter if its king
    )
  }

  case object ReserveToNextTableau extends Setup {
    val sourceElement = ElementInContainer(Reserve, 1)
    val targetElement = Some(ElementInContainer(Tableau, 2))

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      InitializeStep(targetElement.get, CardCreate(Clubs, Two)),  //put Two in target
      MovingCardStep(CardCreate(Hearts, Ace)),        // move Ace to target
    )
  }

  case object ReserveToEmptyFoundation extends Setup {
    val sourceElement = ElementInContainer(Reserve, 1)
    val targetElement = Some(ElementInContainer(Foundation, 2))

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      MovingCardStep(CardCreate(Clubs, Ace)),        // move ACE to target
    )
  }
  case object ReserveToNextFoundation extends Setup {
    val sourceElement = ElementInContainer(Reserve, 1)
    val targetElement = Some(ElementInContainer(Foundation, 2))

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      InitializeStep(targetElement.get, CardCreate(Clubs, Ace)),  // add ace to target
      MovingCardStep(CardCreate(Clubs, Two)),        // move two to target
    )
  }

  val nomad:Solitaire = {
    Solitaire(name = "Nomad",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq(FreeCell),
      moves = Seq(tableauToTableauMove, buildFoundation, flipMove, foundationToTableauMove, deckDealMove, moveToFreeCell, moveFoundationToFreeCell, moveFreeCellToFoundation),
      logic = BoardState(Map(Foundation -> 104)),
      customizedSetup = Seq(TableauToEmptyTableau, TableauToNextTableau, TableauToEmptyFoundation, TableauToNextFoundation,
        ReserveToReserve, ReserveToEmptyFoundation, ReserveToEmptyTableau, ReserveToNextFoundation,
        ReserveToNextTableau, TableauToEmptyReserve,
        TableauToTableauMultipleCards, TableauToEmptyTableauMultipleCards)
    )
  }
}