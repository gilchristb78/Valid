package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.bakersdozen.variationPoints

package object castles_in_spain extends variationPoints{

  override def buildOnEmptyTableau(card: MovingCard.type): Constraint = {NotConstraint(IsAce(card))}

  override def buildOnTableau(card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    AndConstraint(NextRank(topDestination, card), OppositeColor(topDestination, card))
  }

  val castles_in_spain: Solitaire = {
    Solitaire(name = "Castles_In_Spain",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove),
      logic = BoardState(Map(Foundation -> 52)),
      solvable = false
    )
  }
}