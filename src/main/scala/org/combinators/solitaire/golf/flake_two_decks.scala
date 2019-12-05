package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.golf.variationPoints

package object flake_two_decks extends variationPoints{

  override def getNumTableau(): Int = 6
  override def getNumDecks(): Int = 2
  override def golfLayout():Layout = {
    Layout(Map(
      Tableau -> horizontalPlacement(120, 20, getNumTableau(), 5*card_height),
      Waste -> horizontalPlacement(15, 40 + card_height, 1, card_height)
    ))
  }

  override def getDeal(): Seq[DealStep] = {
    Seq(DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)))
  }

  val tableauToTableauMove:Move = SingleCardMove("MoveTableauToTableau", Drag,
    source=(Tableau,Truth), target=Some((Tableau, wasteMove)))

  val flake_two_decks:Solitaire = {

    Solitaire( name="Flake_two_decks",
      structure = map,
      layout = golfLayout(),
      deal = getDeal(),
      specializedElements = Seq(WastePile),
      moves = Seq(tableauToWasteMove, tableauToTableauMove),
      logic = BoardState(Map(Waste -> 104)),
      testSetup = Seq(),
    )
  }
}
