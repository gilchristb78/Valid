package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.fan.variationPoints
import org.combinators.templating.twirl.Java

package object fanfreepile extends variationPoints {

  case object FreePile extends Element(true)

  override val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](18)(Column),
    Foundation -> Seq.fill[Element](4)(Pile),
    Reserve -> Seq.fill[Element](2)(FreePile),
    StockContainer -> Seq(Stock(1))
  )
  override val layoutMap:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> calculatedPlacement(points, height = card_height*2),
    Foundation -> horizontalPlacement(200, 10, 4, card_height),
    Reserve -> calculatedPlacement(Seq((800,250),(800, 250+card_height+card_gap)))
  )

  // ambiguity: two kinds of moves to freePile
  val fromTableauToReserve:Move = SingleCardMove("TableauToReserve", Drag,
    source=(Tableau,Truth), target=Some((Reserve, IsEmpty(Destination))))

  // should be "NotEmpty" not Truth
  val fromReserveToReserve:Move = SingleCardMove("ReserveToReserve", Drag,
    source=(Reserve,Truth), target=Some((Reserve, IsEmpty(Destination))))

  val fromReserveToTableau:Move = SingleCardMove("ReserveToTableau", Drag,
    source=(Reserve,Truth), target=Some((Tableau, tt_move)))

  val fromReserveToFoundation:Move = SingleCardMove("ReserveToFoundation", Drag,
    source=(Reserve,Truth), target=Some((Foundation, tf_move)))

  trait TableauFoundationMoves extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Foundation, 0))
  }

  /** Clear 0th Foundation and prep to move Ace */
  case object TableauToEmptyFoundation extends TableauFoundationMoves {

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      MovingCardStep(CardCreate(Clubs, Ace)),             // Drag ACE to empty spot
    )
  }

  /** Clear 0th Foundation and prep to move Two */
  case object TableauToNextFoundation extends TableauFoundationMoves {

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      InitializeStep(targetElement.get, CardCreate(Clubs, Ace)),   // place ACE in target
      MovingCardStep(CardCreate(Clubs, Two)),                      // Drag Two to follow-up with Ace
    )
  }

  /**
    * Moving from Reserve to anywhere but Tableau will through an error
    * Views from element as
    */
  val fanfreepile:Solitaire = {
    Solitaire(name = "FanFreePile",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal,
      specializedElements = Seq(FreePile),
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, fromTableauToReserve, fromReserveToReserve, fromReserveToTableau,fromReserveToFoundation ),
      logic = BoardState(Map(Tableau -> 0, Foundation -> 52)),
      solvable = true,
      customizedSetup = Seq(TableauToEmptyFoundation, TableauToNextFoundation),
    )
  }
}
