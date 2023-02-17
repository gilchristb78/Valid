package org.combinators.solitaire.fan

import org.combinators.solitaire.domain._

package object fanfreepile extends variationPoints {

  case object FreePile extends Element(true)

  override val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](18)(Column),
    Foundation -> Seq.fill[Element](4)(Pile),
    Reserve -> Seq.fill[Element](2)(FreePile),
    StockContainer -> Seq(Stock())
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

  trait TableauTableauMoves extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Tableau, 0))
  }
  trait TableauReserveMoves extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Reserve, 0))
  }
  trait ReserveReserveMoves extends Setup {
    val sourceElement = ElementInContainer(Reserve, 1)
    val targetElement = Some(ElementInContainer(Reserve, 0))
  }
  trait ReserveTableauMoves extends Setup {
    val sourceElement = ElementInContainer(Reserve, 1)
    val targetElement = Some(ElementInContainer(Tableau, 0))
  }
  trait ReserveFoundationMoves extends Setup {
    val sourceElement = ElementInContainer(Reserve, 1)
    val targetElement = Some(ElementInContainer(Foundation, 0))
  }
//
//  /** Clear 0th Tableau and add King */
//  case object TableauToEmptyTableau extends TableauFoundationMoves {
//
//    val setup:Seq[SetupStep] = Seq(
//      RemoveStep(sourceElement),
//      RemoveStep(targetElement.get),
//      MovingCardStep(CardCreate(Clubs, King)),                      // Drag King to empty Tableau
//    )
//  }
//
//  /** Clear 0th Foundation and prep to move Ace */
//  case object TableauToEmptyFoundation extends TableauFoundationMoves {
//
//    val setup:Seq[SetupStep] = Seq(
//      RemoveStep(sourceElement),
//      MovingCardStep(CardCreate(Clubs, Ace)),             // Drag ACE to empty spot
//    )
//  }
//
//  /** Clear 0th Foundation and prep to move Two */
//  case object TableauToNextFoundation extends TableauFoundationMoves {
//
//    val setup:Seq[SetupStep] = Seq(
//      RemoveStep(sourceElement),
//      InitializeStep(targetElement.get, CardCreate(Clubs, Ace)),   // place ACE in target
//      MovingCardStep(CardCreate(Clubs, Two)),                      // Drag Two to follow-up with Ace
//    )
//  }
//
//  /** Clear 0th Tableau and add Ace, prep to move Two */
//  case object TableauToNextTableau extends TableauTableauMoves {
//
//    val setup:Seq[SetupStep] = Seq(
//      RemoveStep(sourceElement),
//      RemoveStep(targetElement.get),
//      InitializeStep(targetElement.get, CardCreate(Clubs, Two)),   // place Two in target
//      MovingCardStep(CardCreate(Clubs, Ace)),                      // move ACE to target
//    )
//  }

  /** Clear 0th Reserve and add Three because any card can go in a reserve */
  case object TableauToEmptyReserve extends TableauReserveMoves {

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),  //this should clear the 0th Reserve
      MovingCardStep(CardCreate(Clubs, Three)),                      // move Three to target
    )
  }

  /** Clear 0th Reserve and add Three to 1st reserve because any card can go in a reserve */
  case object ReserveToReserve extends ReserveReserveMoves {

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      MovingCardStep(CardCreate(Clubs, Three)),        // move Three to target
    )
  }

  /** Clear 0th Reserve and prep to move King to empty tableau */
  case object ReserveToEmptyTableau extends ReserveTableauMoves {

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      MovingCardStep(CardCreate(Clubs, King)),        // move King to target
    )
  }

  /** Clear 0th Reserve and prep to move two */
  case object ReserveToNextTableau extends ReserveTableauMoves {

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      InitializeStep(targetElement.get, CardCreate(Clubs, Two)),  //put Two in target
      MovingCardStep(CardCreate(Clubs, Ace)),        // move Ace to target
    )
  }

  /** Clear 0th Reserve and add to move ace to foundation */
  case object ReserveToEmptyFoundation extends ReserveFoundationMoves {

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      MovingCardStep(CardCreate(Clubs, Ace)),        // move ACE to target
    )
  }

  /** Clear 0th Reserve and prep to move two */
  case object ReserveToNextFoundation extends ReserveFoundationMoves {

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      InitializeStep(targetElement.get, CardCreate(Clubs, Ace)),  // add ace to target
      MovingCardStep(CardCreate(Clubs, Two)),        // move two to target
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
      customizedSetup = Seq(TableauToEmptyTableau, TableauToNextTableau,
        TableauToEmptyFoundation, TableauToNextFoundation,
        TableauToEmptyReserve,
        ReserveToReserve,
        ReserveToEmptyTableau, ReserveToNextTableau,
        ReserveToEmptyFoundation, ReserveToNextFoundation
      ),
    )
  }
}
