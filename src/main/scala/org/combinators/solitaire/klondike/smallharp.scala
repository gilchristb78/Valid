package org.combinators.solitaire.klondike

import org.combinators.solitaire.domain._

package object smallharp extends variationPoints {

  override val klondikeLayout:Map[ContainerType, Seq[Widget]] = Map (
    Foundation -> horizontalPlacement(10, 10, 4, card_height),
    StockContainer -> horizontalPlacement(440, 10, 1, card_height),
    Tableau -> horizontalPlacement(10, 200, num = 7, height = card_height*8),  // estimate
    Waste -> horizontalPlacement(460 + card_width, 10, 1, card_height),
  )

  def smallHarpDeal: Seq[DealStep] = {
    var deal:Seq[DealStep] = Seq()

    // each of the BuildablePiles gets a number of facedown cards, 0 to first Pile, 1 to second pile, etc...
    // don't forget zero-based indexing.
    for (pileNum <- 0 until 6) {
      deal = deal :+ DealStep(ElementTarget(Tableau, pileNum), new Payload(false, 6-pileNum))
    }

    // finally each one gets a single faceup Card, and deal one to waste pile
    //add(new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau), new Payload()));
    deal = deal :+ DealStep(ContainerTarget(Tableau))

    // finally to deal cards
    deal = deal :+ DealStep(ContainerTarget(Waste), Payload(true, numToDeal))
    deal
  }

  val smallharp:Solitaire = {

    Solitaire( name="SmallHarp",
      structure = klondikeMap,
      layout = Layout(klondikeLayout),
      deal = smallHarpDeal,

      specializedElements = Seq(WastePile),

      moves = Seq(tableauToTableau, wasteToTableau, tableauToFoundation, wasteToFoundation, deckDeal, deckResetFromWaste, flipTableau),

      // fix winning logic
      logic = BoardState(Map(Foundation -> 52)),
      customizedSetup = Seq.empty
    )
  }
}
