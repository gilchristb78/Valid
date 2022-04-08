package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.fan._

package object superflowergarden extends variationPoints {

  def buildOnTableau2(card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    NextRank(topDestination, card)
  }

//  val tt_move2:Constraint = AndConstraint(NotConstraint(IsEmpty(Destination)), buildOnTableau2(MovingCard))
//
//  val tableauToTableauMove2:Move = SingleCardMove("MoveCard", Drag,
//    source=(Tableau,Truth), target=Some((Tableau, tt_move2)))

  val superflowergarden: Solitaire = {
    Solitaire(name = "Superflowergarden",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove),  //tableauToTableauMove2
      logic = BoardState(Map(Tableau -> 0, Foundation -> 52)),
      solvable = true,
      customizedSetup = Seq(TableauToEmptyFoundation, TableauToNextFoundation, TableauToEmptyTableau, TableauToNextTableau)
    )
  }
}