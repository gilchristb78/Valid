package org.combinators.solitaire.klondike

import org.combinators.solitaire.domain._

package object eastcliff extends variationPoints {


  def eastcliffDeal: Seq[DealStep] = {
    var deal:Seq[DealStep] = Seq()

    // each of the BuildablePiles gets a number of facedown cards, 0 to first Pile, 1 to second pile, etc...
    // don't forget zero-based indexing.
    for (pileNum <- 1 until 7) {
      deal = deal :+ DealStep(ElementTarget(Tableau, pileNum), new Payload(false, 2))
      deal = deal :+ DealStep(ElementTarget(Tableau, pileNum), new Payload(true, 1))
    }

    // finally to deal cards
    deal = deal :+ DealStep(ContainerTarget(Waste), Payload(true, numToDeal))
    deal
  }

  val eastcliff:Solitaire = {

    Solitaire( name="EastCliff",
      structure = klondikeMap,
      layout = Layout(klondikeLayout),
      deal = eastcliffDeal,

      specializedElements = Seq(WastePile),

      moves = Seq(tableauToTableau, wasteToTableau, tableauToFoundation, wasteToFoundation, deckDeal, deckResetFromWaste, flipTableau),

      // fix winning logic
      logic = BoardState(Map(Foundation -> 52)),
      customizedSetup = Seq.empty
    )
  }
}
