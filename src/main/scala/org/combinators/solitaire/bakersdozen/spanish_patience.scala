package org.combinators.solitaire

import org.combinators.solitaire.bakersdozen.variationPoints
import org.combinators.solitaire.domain._

package object spanish_patience extends variationPoints {

  override def buildOnEmptyTableau(card: MovingCard.type): Constraint = {NotConstraint(IsAce(card))}

  val spanish_patience:Solitaire = {
    Solitaire(name = "Spanish_Patience",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove),  //deckDealMove before flipmove and in varpoints. End with flipmove
      logic = BoardState(Map(Foundation -> 52)),
      solvable = false
    )
  }
}