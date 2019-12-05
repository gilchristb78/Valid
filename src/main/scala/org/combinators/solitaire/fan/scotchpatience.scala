package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.fan.variationPoints

import scala.util.Random


package object scotchpatience extends variationPoints {

  override def buildOnTableau (card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    NextRank(card, topDestination)
  }

  override def buildOnFoundation (card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    AndConstraint( NextRank(card, topDestination),  OppositeColor(card, topDestination))
  }


  val scotchpatience:Solitaire = {
    Solitaire(name = "ScotchPatience",
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
