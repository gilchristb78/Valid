package org.combinators.solitaire.klondike

import org.combinators.solitaire.domain._

package object whitehead extends variationPoints {

  case class AllSameSuit(movingCards: MoveInformation) extends Constraint

  // Whitehead is played just like Klondike but limited to a single pass through the stock (no redeals allowed)
  // and with stacks built down by color, however only stacks built down by the same suit can be moved together.
  override def TableauToTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomMoving = BottomCardOf(cards)
    val isEmpty = IsEmpty(Destination)
    val descend = Descending(cards)
    val samesuit = AllSameSuit(cards)
    OrConstraint(AndConstraint(isEmpty, descend, samesuit),
      AndConstraint(descend, samesuit, NextRank(topDestination, bottomMoving), SameColor(topDestination, bottomMoving)))
  }

  override def WasteToTableau: Constraint = OrConstraint(IsEmpty(Destination),
    AndConstraint(SameColor(TopCardOf(Destination), MovingCard), NextRank(TopCardOf(Destination), MovingCard)))

  val whitehead:Solitaire = {

    Solitaire( name="WhiteHead",
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
