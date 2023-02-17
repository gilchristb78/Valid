package org.combinators.solitaire.freecell

import org.combinators.solitaire.domain._

/**
 * Four initial cards dealt to the reserve. Only kings allowed in empty tableaus
 */
package object forecell extends variationPoints  {


  // rules for moving to an empty tableau.
  override def buildOnEmptyTableauSingle(card:MovingCard.type) : Constraint = {
    IsKing(card)
  }
  //
  override def buildOnEmptyTableauMultiple(cards: MovingCards.type) : Constraint = {
    IsKing(BottomCardOf(cards))
  }

  def forecellDeal: Seq[DealStep] = {

      // deal four to reserve
      var deal:Seq[DealStep] = Seq(DealStep(ContainerTarget(Reserve)))
      var colNum = 0

      // Deal cards to all columns (from left to right) until none left, in which case
      // only first four have the extra cards.
      var numDealt = 4
      while (numDealt < 52) {
        deal = deal :+ DealStep(ElementTarget(Tableau, colNum), Payload())
        colNum += 1
        numDealt += 1
        if (colNum > 7) { colNum = 0}
      }

      deal
    }

  val forecell:Solitaire = {

    Solitaire(name="ForeCell",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = forecellDeal,
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
