package org.combinators.solitaire.fan

import org.combinators.solitaire.domain._

package object scotchpatience extends variationPoints {

  override def buildOnTableau (card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    NextRank(topDestination, card)   // suit not relevant...
  }

  override def buildOnFoundation (card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    AndConstraint(NextRank(card, topDestination),  OppositeColor(card, topDestination)) //can be the opposite color
  }

//  /**
//    * Clear 0th Foundation and place [Two, Ace] on the 0th Tableau
//    */
//  case object PrepareTableauToFoundation extends Setup {
//    val sourceElement = ElementInContainer(Tableau, 0)
//    val targetElement = Some(ElementInContainer(Foundation, 2))
//
//    // clear Foundation, and place [2C, AC] on 0th tableau
//    val setup:Seq[SetupStep] = Seq(
//      RemoveStep(ElementInContainer(Tableau, 1)),
//      RemoveStep(ElementInContainer(Tableau, 2)),
//      InitializeStep(ElementInContainer(Tableau, 2), CardCreate(Clubs, Ace)),
//      InitializeStep(ElementInContainer(Tableau, 2), CardCreate(Hearts, Two)),
//      InitializeStep(ElementInContainer(Foundation, 2), CardCreate(Spades, Ace)),
//      InitializeStep(ElementInContainer(Foundation, 2), CardCreate(Diamonds, Two)),
//    )
//  }

  case object TableauToNextTableauIgnoreSuit extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Tableau, 2))

    override val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      InitializeStep(targetElement.get, CardCreate(Clubs, Three)),
      MovingCardStep(CardCreate(Hearts, Two))
    )
  }
  case object TableauToNextFoundationOppositeColor extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Foundation, 2))

    override val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      InitializeStep(targetElement.get, CardCreate(Clubs, Ace)),
      MovingCardStep(CardCreate(Hearts, Two))
    )
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
      customizedSetup = Seq(//PrepareTableauToFoundation,
        TableauToNextTableauIgnoreSuit, TableauToNextFoundationOppositeColor,
        TableauToEmptyFoundation, TableauToNextFoundation, //shouldn't pass - has to be opposite color
        TableauToEmptyTableau, TableauToNextTableau)
    )
  }
}
