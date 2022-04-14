package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.fan._

package object superflowergarden extends variationPoints {

  def buildOnTableau2(card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    NextRank(topDestination, card) //suit doesn't matter
  }

//  val tt_move2:Constraint = AndConstraint(NotConstraint(IsEmpty(Destination)), buildOnTableau2(MovingCard))
//
//  val tableauToTableauMove2:Move = SingleCardMove("MoveCard", Drag,
//    source=(Tableau,Truth), target=Some((Tableau, tt_move2)))

  case object TableauToNextTableauIgnoreSuit extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Tableau, 2))

    override val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      InitializeStep(targetElement.get, CardCreate(Clubs, Ace)),
      InitializeStep(targetElement.get, CardCreate(Clubs, Two)),
      MovingCardStep(CardCreate(Hearts, Three))
    )
  }

  val superflowergarden: Solitaire = {
    Solitaire(name = "Superflowergarden",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal,
      specializedElements = Seq.empty, //should have a redeal
      moves = Seq(tableauToTableauMove, tableauToFoundationMove),  //tableauToTableauMove2
      logic = BoardState(Map(Tableau -> 0, Foundation -> 52)),
      solvable = true,
      customizedSetup = Seq(TableauToEmptyFoundation, TableauToNextFoundation, TableauToEmptyTableau, TableauToNextTableauIgnoreSuit, TableauToNextTableau)
    )
  }
}