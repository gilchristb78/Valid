package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.gypsy.variationPoints


package object irmgard extends variationPoints {
  override def getNumTableau(): Int = 9
  override def getDeal(): Seq[DealStep] = {
    Seq(DealStep(ElementTarget(Tableau, 0)),
      DealStep(ElementTarget(Tableau, 1), Payload(faceUp = false)),
      DealStep(ElementTarget(Tableau, 1)),
      DealStep(ElementTarget(Tableau, 2), Payload(numCards = 2, faceUp = false)),
      DealStep(ElementTarget(Tableau, 2)),
      DealStep(ElementTarget(Tableau, 3), Payload(numCards = 3, faceUp = false)),
      DealStep(ElementTarget(Tableau, 3)),
      DealStep(ElementTarget(Tableau, 4), Payload(numCards = 4, faceUp = false)),
      DealStep(ElementTarget(Tableau, 4)),
      DealStep(ElementTarget(Tableau, 5), Payload(numCards = 3, faceUp = false)),
      DealStep(ElementTarget(Tableau, 5)),
      DealStep(ElementTarget(Tableau, 6), Payload(numCards = 2, faceUp = false)),
      DealStep(ElementTarget(Tableau, 6)),
      DealStep(ElementTarget(Tableau, 7), Payload(numCards = 1, faceUp = false)),
      DealStep(ElementTarget(Tableau, 7)),
      DealStep(ElementTarget(Tableau, 8))
    )
  }

  override def buildOnTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomMoving = BottomCardOf(cards)
    val isEmpty = IsEmpty(Destination)
    val descend = Descending(cards)
    val suit = AlternatingColors(cards)
    OrConstraint(AndConstraint(isEmpty, descend, suit, IsKing(bottomMoving)), AndConstraint(descend, suit, OppositeColor(topDestination, bottomMoving)))
  }

  override def foundationToTableauConstraint():Constraint = OrConstraint(
    AndConstraint(IsEmpty(Destination), IsKing(MovingCard)),
    AndConstraint(
      OppositeColor(MovingCard, TopCardOf(Destination)),
      NextRank(TopCardOf(Destination), MovingCard))
  )

  val irmgard:Solitaire = {
    Solitaire(name = "Irmgard",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, buildFoundation, foundationToTableauMove, flipMove, deckDealMove),
      logic = BoardState(Map(Foundation -> 104)),
      solvable = false,
      testSetup = Seq(),
    )
  }
}