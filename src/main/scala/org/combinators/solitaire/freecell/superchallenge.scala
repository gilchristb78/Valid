package org.combinators.solitaire.freecell

import org.combinators.solitaire.domain._

/**
 * Any time you change a twirl template, you HAVE to force a regeneration. Unfortunately, this
 * is accomplished ONLY by launching a web server and connecting via localhost:9000
 */
package object superchallenge extends variationPoints  {

  // rules for moving to an empty tableau.
  override def buildOnEmptyTableauSingle(card:MovingCard.type) : Constraint = {
    IsKing(card)
  }
  //
  override def buildOnEmptyTableauMultiple(cards: MovingCards.type) : Constraint = {
    IsKing(BottomCardOf(cards))
  }

  val superchallengeFreecell:Solitaire = {

    Solitaire(name="SuperChallengeFreeCell",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = challenge.challengeDeal,
      /** from element can infer ks.ViewWidget as well as Base Element. */
      specializedElements = Seq(FreeCellPile),

      /** All rules here. */
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, fromTableauToReserve, fromReserveToReserve, fromReserveToTableau, fromReserveToFoundation ),
      // fix winning logic
      logic = BoardState(Map(Foundation -> 52)),
      customizedSetup = Seq.empty,

      solvable = true,
      autoMoves = true   // handle auto moves!
    )
  }
}
