package org.combinators.solitaire.freecell

import org.combinators.solitaire.domain._

/**
 * Any time you change a twirl template, you HAVE to force a regeneration. Unfortunately, this
 * is accomplished ONLY by launching a web server and connecting via localhost:9000
 */
package object regular extends variationPoints {

  def getDeal: Seq[DealStep] = {
    var deal:Seq[DealStep] = Seq()
    var colNum = 0
    // Deal cards to all columns (from left to right) until none left, in which case
    // only first four have the extra cards.
    var numDealt = 0
    while (numDealt < 52*numDecks) {
      deal = deal :+ DealStep(ElementTarget(Tableau, colNum), Payload())
      colNum += 1
      numDealt += 1
      if (colNum > 7) { colNum = 0}
    }

    deal
  }

  val freecell:Solitaire = {

    Solitaire(name="FreeCell",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal,
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
