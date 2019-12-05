package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.fan._

package object labellelucie extends variationPoints{

  val tt_move2:Constraint = AndConstraint(NotConstraint(IsEmpty(Destination)), buildOnTableau(MovingCard))

  val tableauToTableauMove2:Move = SingleCardMove("MoveCard", Drag,
    source=(Tableau,Truth), target=Some((Tableau, tt_move2)))

  val labellelucie: Solitaire = {
    Solitaire(name = "labellelucie",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove2, tableauToFoundationMove),
      logic = BoardState(Map(Tableau -> 0, Foundation -> 52)),
      solvable = true,
      testSetup = Seq(),
    )
  }
}