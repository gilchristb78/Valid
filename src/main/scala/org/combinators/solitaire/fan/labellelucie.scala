package org.combinators.solitaire.fan

import org.combinators.solitaire.domain._

package object labellelucie extends variationPoints{

  val tt_move2:Constraint = AndConstraint(NotConstraint(IsEmpty(Destination)), buildOnTableau(MovingCard))

  val tableauToTableauMove2:Move = SingleCardMove("MoveCard", Drag,
    source=(Tableau,Truth), target=Some((Tableau, tt_move2)))

  val labellelucie: Solitaire = {
    Solitaire(name = "LabelleLucie",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal,
      specializedElements = Seq.empty, //should have a redeal
      moves = Seq(tableauToTableauMove2, tableauToFoundationMove),
      logic = BoardState(Map(Tableau -> 0, Foundation -> 52)),
      solvable = true,
      customizedSetup = Seq(TableauToEmptyFoundation, TableauToNextFoundation, TableauToEmptyTableau, TableauToNextTableau)
    )
  }
}