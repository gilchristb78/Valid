package org.combinators.solitaire.gypsy

import org.combinators.solitaire.domain._

/** Defines Gypsy's variation points
  */
trait variationPoints {
  case class AllSameSuit(movingCards: MoveInformation) extends Constraint
  def getNumStock:Int = 2
  def getNumTableau:Int = 8
  def getNumFoundation:Int = 8

  val map:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> horizontalPlacement(15, 200, getNumTableau, 13*card_height),
    StockContainer -> horizontalPlacement(15, 20, 1, card_height),
    Foundation -> horizontalPlacement(100, 20, getNumFoundation, card_height)
  )

  val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](getNumTableau)(BuildablePile),
    Foundation -> Seq.fill[Element](getNumFoundation)(Pile),
    StockContainer -> Seq(Stock(getNumStock))
  )

  def getDeal: Seq[DealStep] = {
    Seq(DealStep(ContainerTarget(Tableau), Payload(faceUp = false, numCards = 2)),
      DealStep(ContainerTarget(Tableau)))
  }

  def buildOnTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomMoving = BottomCardOf(cards)
    val isEmpty = IsEmpty(Destination)
    val descend = Descending(cards)
    val suit = AlternatingColors(cards)
    OrConstraint(AndConstraint(isEmpty, descend, suit), AndConstraint(descend, suit, OppositeColor(topDestination, bottomMoving)))
  }

  val tf_tgt = IfConstraint(IsEmpty(Destination),
    AndConstraint (IsSingle(MovingCards), IsAce(BottomCardOf(MovingCards))),
    AndConstraint (IsSingle(MovingCards),
      NextRank(BottomCardOf(MovingCards), TopCardOf(Destination)),
      SameSuit(BottomCardOf(MovingCards), TopCardOf(Destination))))

  val buildFoundation:Move = MultipleCardsMove("BuildFoundation", Drag,
    source=(Tableau, Truth), target=Some((Foundation, tf_tgt)))

  def foundationToTableauConstraint:Constraint = OrConstraint(
    IsEmpty(Destination),
    AndConstraint(
      OppositeColor(MovingCard, TopCardOf(Destination)),
      NextRank(TopCardOf(Destination), MovingCard))
  )

  val tableauToTableauMove:Move = MultipleCardsMove("MoveColumn", Drag,
    source=(Tableau, Truth), target=Some((Tableau, buildOnTableau(MovingCards))))

  val foundationToTableauMove:Move = SingleCardMove("MoveFoundationToTableau", Drag,
    source=(Foundation,Truth), target=Some((Tableau, foundationToTableauConstraint)))

  val deckDealMove:Move = DealDeckMove("DealDeck", 1,
    source=(StockContainer, NotConstraint(IsEmpty(Source))), target=Some((Tableau, Truth)))

  val allowed = AndConstraint(NotConstraint(IsEmpty(Source)), NotConstraint(IsFaceUp(TopCardOf(Source))))
  val flipMove:Move = FlipCardMove("FlipCard", Press, source = (Tableau, allowed))


  case object TableauToEmptyFoundation extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Foundation, 2))

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
        MovingCardStep(CardCreate(Clubs, Eight))
    )

  }
  case object TableauToNextFoundation extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Foundation, 2))

    // clear Foundation, and place [2C, AC] on 1st tableau
    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      InitializeStep(targetElement.get, CardCreate(Clubs, Ace)),
      InitializeStep(targetElement.get, CardCreate(Clubs, Two)),
      //MovingCardsStep(Seq(CardCreate(Clubs, Three)))
      MovingCardStep(CardCreate(Clubs, Eight))
    )
  }

  case object TableauToEmptyTableau extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Tableau, 2))

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      //MovingCardsStep(Seq(CardCreate(Clubs, Four)))
      MovingCardStep(CardCreate(Clubs, Eight))
    )
  }

  case object TableauToNextTableau extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Tableau, 2))

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      InitializeStep(targetElement.get, CardCreate(Clubs, Three)),
      InitializeStep(targetElement.get, CardCreate(Hearts, Two)),
      //MovingCardsStep(Seq(CardCreate(Clubs, Ace)))
      MovingCardStep(CardCreate(Clubs, Eight))
    )
  }

  case object TableauToTableauMultipleCards extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Tableau, 2))

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      InitializeStep(targetElement.get, CardCreate(Clubs, Ten)),
      InitializeStep(targetElement.get, CardCreate(Hearts, Nine)),
      MovingCardStep(CardCreate(Clubs, Eight))
      //MovingCardsStep(Seq(CardCreate(Clubs, Eight), CardCreate(Diamonds, Seven)))
    )
  }

  case object TableauToEmptyTableauMultipleCards extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Tableau, 2))

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      MovingCardStep(CardCreate(Clubs, Eight))
     // MovingCardsStep(Seq(CardCreate(Clubs, Eight), CardCreate(Diamonds, Seven)))
    )
  }

  case object DealDeck {

    val setup:Seq[SetupStep] = Seq(

    )
  }

}