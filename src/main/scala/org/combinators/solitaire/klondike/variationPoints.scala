package org.combinators.solitaire.klondike

import org.combinators.solitaire.domain._

trait variationPoints {

  val numToDeal:Int = 1

  val klondikeMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](7)(BuildablePile),
    Waste -> Seq.fill[Element](1)(WastePile),
    Foundation -> Seq.fill[Element](4)(Pile),
    StockContainer -> Seq(Stock())
  )

  val klondikeLayout:Map[ContainerType, Seq[Widget]] = Map (
    StockContainer -> horizontalPlacement(10, 10, 1, card_height),
    Foundation -> horizontalPlacement(240, 10, 4, card_height),
    Tableau -> horizontalPlacement(10, 200, num = 7, height = card_height*8),  // estimate
    Waste -> horizontalPlacement(20 + card_width, 10, 1, card_height),
  )

  def klondikeDeal: Seq[DealStep] = {
    var deal:Seq[DealStep] = Seq()

    // each of the BuildablePiles gets a number of facedown cards, 0 to first Pile, 1 to second pile, etc...
    // don't forget zero-based indexing.
    for (pileNum <- 1 until 7) {
      deal = deal :+ DealStep(ElementTarget(Tableau, pileNum), new Payload(false, pileNum))
    }

    // finally each one gets a single faceup Card, and deal one to waste pile
    //add(new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau), new Payload()));
    deal = deal :+ DealStep(ContainerTarget(Tableau))

    // finally to deal cards
    deal = deal :+ DealStep(ContainerTarget(Waste), Payload(true, numToDeal))
    deal
  }


  // Klondike consists of following actionable elements
  //
  //   WastePile,    Foundation
  //   Tableau
  //
  // And you can move cards from
  // 1. TableauToTableau
  // 2. TableauToFoundation
  // 3. WastePileToTableau
  // 4. WastePileToFoundation
  // 5. FlipCard on Tableau
  // 6. DealDeck
  // 7. ResetDeck

  /** 1. Logic to determine how Moving cards are to be built on tableau. */
  def TableauToTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomMoving = BottomCardOf(cards)
    val isEmpty = IsEmpty(Destination)
    val descend = Descending(cards)
    val alternating = AlternatingColors(cards)
    OrConstraint(AndConstraint(isEmpty, descend, alternating),
      AndConstraint(descend, alternating, NextRank(topDestination, bottomMoving), OppositeColor(topDestination, bottomMoving)))
  }
  val tableauToTableau:Move = MultipleCardsMove("MoveColumn", Drag,
    source=(Tableau, Truth), target=Some((Tableau, TableauToTableau(MovingCards))))

  /** 2. Logic to determine how Moving cards are to be built on foundation from tableau. */
  def TableauToFoundation:Constraint = IfConstraint(IsEmpty(Destination),
    AndConstraint (IsSingle(MovingCards), IsAce(BottomCardOf(MovingCards))),
    AndConstraint (IsSingle(MovingCards),
      NextRank(BottomCardOf(MovingCards), TopCardOf(Destination)),
      SameSuit(BottomCardOf(MovingCards), TopCardOf(Destination))))
  val tableauToFoundation:Move = MultipleCardsMove("BuildFoundation", Drag,
    source=(Tableau, Truth), target=Some((Foundation, TableauToFoundation)))

  /** 3. Logic to determine how cards are moved from waste to tableau. */
  def WasteToTableau: Constraint = OrConstraint(IsEmpty(Destination),
    AndConstraint(OppositeColor(TopCardOf(Destination), MovingCard), NextRank(TopCardOf(Destination), MovingCard)))
  val wasteToTableau:Move = SingleCardMove("MoveCard", Drag,
    source=(Waste,Truth), target=Some(Tableau, WasteToTableau))

  /** 4. Logic to determine how cards are moved from waste to foundation. */
  def WasteToFoundation:Constraint =  IfConstraint(IsEmpty(Destination),
    AndConstraint (IsSingle(MovingCard), IsAce(MovingCard)),
    AndConstraint(NextRank(MovingCard, TopCardOf(Destination)),
      SameSuit(MovingCard, TopCardOf(Destination))))
  val wasteToFoundation:Move = SingleCardMove("BuildFoundationFromWaste", Drag,
    source=(Waste, Truth), target=Some((Foundation, WasteToFoundation)))

  /** 5. Flip Card - could be made generic? */
  def FlipTableau:Constraint = AndConstraint(NotConstraint(IsEmpty(Source)), NotConstraint(IsFaceUp(TopCardOf(Source))))
  val flipTableau:Move = FlipCardMove("FlipCard", Press, source = (Tableau, FlipTableau))

  /** 6. Deal cards from Deck if not empty. */
  def DeckDeal:Constraint = NotConstraint(IsEmpty(Source))
  val deckDeal:Move = DealDeckMove("DealDeck", numToDeal,
    source=(StockContainer, DeckDeal), target=Some((Waste, Truth)))

  /** 7. Reset Deck from Waste Pile. */
  val deckResetFromWaste: Move = ResetDeckMove("ResetDeck",
    source = (StockContainer, IsEmpty(Source)), target = Some((Waste, Truth)))
}
