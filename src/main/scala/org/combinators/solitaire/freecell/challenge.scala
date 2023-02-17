package org.combinators.solitaire.freecell

import org.combinators.solitaire.domain._

/**
 * Any time you change a twirl template, you HAVE to force a regeneration. Unfortunately, this
 * is accomplished ONLY by launching a web server and connecting via localhost:9000
 */
package object challenge extends variationPoints  {

  def challengeDeal: Seq[Step] = {
    val deal:Seq[Step] = Seq (FilterStep(IsAce(DealComponents))) ++ Seq (FilterStep(IsRank(DealComponents, Two)))

    deal ++ regular.getDeal
  }

  val challengeFreecell:Solitaire = {

    Solitaire(name="ChallengeFreeCell",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = challengeDeal,
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
