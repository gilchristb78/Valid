package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.fan.{variationPoints}

package object faneasy extends variationPoints {

  def easyBuildOnTableau(card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    NextRank(topDestination, card)
  }
  val tt_easy_move:Constraint = IfConstraint(IsEmpty(Destination), buildOnEmptyTableau(MovingCard), easyBuildOnTableau(MovingCard))

  override val tableauToTableauMove:Move = SingleCardMove("MoveCard", Drag,
    source=(Tableau,Truth), target=Some((Tableau, tt_easy_move)))

  val faneasy: Solitaire = {
    Solitaire(name = "faneasy",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove),
      logic = BoardState(Map(Tableau -> 0, Foundation -> 52)),
      solvable = true,
      testSetup = Seq(),
    )
  }
}