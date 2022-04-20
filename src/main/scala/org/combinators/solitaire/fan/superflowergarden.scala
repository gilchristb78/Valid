package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.fan._

package object superflowergarden extends variationPoints {

  case object RedealBox extends Element(true)

  override def buildOnTableau(card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    NextRank(topDestination, card) //suit doesn't matter
  }

  override val layoutMap:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> calculatedPlacement(points, height = card_height*2),
    Foundation -> horizontalPlacement(300, 10, 4, card_height),
    Redeal -> horizontalPlacement(100, 10, 1, card_height)
  )

  override val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](18)(Column),
    Foundation -> Seq.fill[Element](4)(Pile),
    Redeal -> Seq(Stock(1))
  )


  case object TableauToNextTableauIgnoreSuit extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Tableau, 2))

    override val setup: Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      InitializeStep(targetElement.get, CardCreate(Clubs, Ace)),
      InitializeStep(targetElement.get, CardCreate(Clubs, Two)),
      MovingCardStep(CardCreate(Hearts, Three))
    )
  }

  val redealMove:Move = ResetDeckMove("ResetDeck", source=(Redeal, Truth), target=Some((Tableau, Truth)))

  val superflowergarden: Solitaire = {
    Solitaire(name = "Superflowergarden",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal,
      specializedElements = Seq(RedealBox), //should have a redeal
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, redealMove),  //tableauToTableauMove2
      logic = BoardState(Map(Tableau -> 0, Foundation -> 52)),
      solvable = true,
      customizedSetup = Seq(TableauToEmptyFoundation, TableauToNextFoundation, TableauToEmptyTableau, TableauToNextTableauIgnoreSuit, TableauToNextTableau)
    )
  }
}