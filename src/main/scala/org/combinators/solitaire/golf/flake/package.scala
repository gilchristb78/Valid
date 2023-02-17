package org.combinators.solitaire.golf

import org.combinators.solitaire.domain._

package object flake extends variationPoints {

  override def getNumTableau: Int = 6

  override def golfLayout(): Layout = {
    Layout(Map(
      Tableau -> horizontalPlacement(120, 20, getNumTableau, 5 * card_height),
      Waste -> horizontalPlacement(15, 40 + card_height, 1, card_height)
    ))
  }

  override def getDeal: Seq[DealStep] = {

    var dealSeq: Seq[DealStep] = Seq() // doesn't like me declaring it without initializing
    //first four piles get 9 cards
    for (colNum <- 0 to 3) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards = 9))
    }
    //the rest get 8 cards
    for (colNum <- 4 to 5) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards = 8))
    }
    dealSeq
  }

  val tableauToTableauMove: Move = SingleCardMove("MoveTableauToTableau", Drag,
    source = (Tableau, Truth), target = Some((Tableau, wasteMove)))

  val definition: Solitaire = {

    Solitaire(name = "Flake",
      structure = map,
      layout = golfLayout(),
      deal = getDeal,
      specializedElements = Seq(WastePile),
      moves = Seq(tableauToWasteMove, tableauToTableauMove),
      logic = BoardState(Map(Waste -> 52))
    )
  }
}
