package org.combinators.solitaire.golf

import org.combinators.solitaire.domain._

package object allInARow extends variationPoints {

  override def getNumTableau: Int = 13

  override def golfLayout(): Layout = {
    Layout(Map(
      Tableau -> horizontalPlacement(120, 20, getNumTableau, 5 * card_height),
      Waste -> horizontalPlacement(15, 40 + card_height, 1, card_height)
    ))
  }

  override def getDeal: Seq[DealStep] = {
    Seq(DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)))
  }

  val definition: Solitaire = {

    Solitaire(name = "AllInARow",
      structure = map,
      layout = golfLayout(),
      deal = getDeal,
      specializedElements = Seq(WastePile),
      moves = Seq(tableauToWasteMove),
      logic = BoardState(Map(Waste -> 52))
    )
  }
}
