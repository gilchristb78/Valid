package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.fan.variationPoints
import org.combinators.solitaire.fanfreepile.TableauToEmptyFoundation
import org.combinators.templating.twirl.Java

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

  /**
    * Clear 0th Foundation and place [Two, Ace] on the 0th Tableau
    */
  case object PrepareTableauToFoundation extends Setup {
    val sourceElement = ElementInContainer(Tableau, 0)
    val targetElement = Some(ElementInContainer(Foundation, 2))

    // clear Foundation, and place [2C, AC] on 0th tableau
    val setup:Seq[SetupStep] = Seq(
      RemoveStep(ElementInContainer(Tableau, 1)),
      RemoveStep(ElementInContainer(Tableau, 2)),
      InitializeStep(ElementInContainer(Tableau, 2), CardCreate(Clubs, Ace)),
      InitializeStep(ElementInContainer(Tableau, 2), CardCreate(Hearts, Two)),
      InitializeStep(ElementInContainer(Foundation, 2), CardCreate(Spades, Ace)),
      InitializeStep(ElementInContainer(Foundation, 2), CardCreate(Diamonds, Two)),
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
      customizedSetup = Seq(PrepareTableauToFoundation),
    )
  }
}
