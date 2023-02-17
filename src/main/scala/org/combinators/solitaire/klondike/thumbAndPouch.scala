package org.combinators.solitaire.klondike

import org.combinators.solitaire.domain._

package object thumbAndPouch extends variationPoints {

  case class AllSameSuit(movingCards: MoveInformation) extends Constraint

  // You may build tableau piles down in any suit but the same. One card or group of cards
  // in the proper sequence can be moved from pile to pile.
  override def TableauToTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomMoving = BottomCardOf(cards)
    val isEmpty = IsEmpty(Destination)
    val descend = Descending(cards)
    OrConstraint(AndConstraint(isEmpty, descend),
                 AndConstraint(descend, NextRank(topDestination, bottomMoving), NotConstraint(SameSuit(topDestination, bottomMoving))))
  }

  override def WasteToTableau: Constraint = OrConstraint(IsEmpty(Destination),
    AndConstraint(NotConstraint(SameSuit(TopCardOf(Destination), MovingCard)), NextRank(TopCardOf(Destination), MovingCard)))

  val thumbandpouch:Solitaire = {

    Solitaire( name="ThumbAndPouch",
      structure = klondikeMap,
      layout = Layout(klondikeLayout),
      deal = klondikeDeal,

      specializedElements = Seq(WastePile),

      moves = Seq(tableauToTableau, wasteToTableau, tableauToFoundation, wasteToFoundation, deckDeal, deckResetFromWaste, flipTableau),

      // fix winning logic
      logic = BoardState(Map(Foundation -> 52)),
      customizedSetup = Seq.empty
    )
  }
}
