package org.combinators.solitaire.domain

/** No longer keep track of index. */
case class Widget( x:Int,  y:Int,  width:Int,  height:Int)

case class Layout(places:Map[ContainerType, Seq[Widget]]) {

  /** Computed once. */
  val allWidgets:Iterable[Widget] = places.values.flatten

  /** Place given widgets for container type, overriding if necessary. */
  def place(ct:ContainerType, widgets:Widget*) : Layout = {
    copy(places = places.updated(ct,widgets))
  }

  /** A variation may choose to remove a given container Type. */
  def remove(ct:ContainerType) : Layout =
    copy(places = places - ct)

  /** Determines if container has any visible Widget. */
  def isVisible(ct:ContainerType) : Boolean =
    places.get(ct).exists(_.nonEmpty)

  /** For layout, find greatest extent. */
  def minimumSize : (Int, Int) = {

    // find max w.y + w.height over all
    // find max w.x + w.width over all
    (allWidgets.map(w => w.x + w.width).max,
      allWidgets.map(w => w.y + w.height).max)
  }
}


trait WinningLogic { }

/** Default logic is to hit a certain score. */
case class ScoreAchieved(score:Int) extends WinningLogic

case class BoardState(states:Map[ContainerType, Int]) extends WinningLogic

sealed trait GestureType

case object Drag extends GestureType
case object Press extends GestureType
case object Click extends GestureType

sealed trait MoveType

case class DealDeck(numCards:Int) extends MoveType
case object ResetDeck extends MoveType
case object MultipleCards extends MoveType
case object FlipCard extends MoveType
case object SingleCard extends MoveType
case object RemoveSingleCard extends MoveType
case object RemoveMultipleCards extends MoveType


case class Move
(
  name:String,
  moveType:MoveType,
  gesture:GestureType,
  movableElement:Element,
  source:(ContainerType,Constraint),
  target:Option[(ContainerType,Constraint)],
  isSingleDestination:Boolean = true,
) {

  def isSingleCard:Boolean = movableElement match {
    case Card => true
    case _ => false
  }

  // join together, if both exist
  def constraints:Constraint = {
    if (target.isDefined) {
      AndConstraint(source._2, target.get._2)
    } else {
      source._2
    }
  }
}



case class Solitaire
(
  /** Every solitaire game has its own name. */
   name:String,

   structure:Map[ContainerType, Seq[Element]],

   layout:Layout,

   deal:Seq[Step],

  specializedElements:Seq[Element],

   /** All rules here. */
   moves:Seq[Move],

   logic:WinningLogic = ScoreAchieved(52),
   autoMoves:Boolean = false,
   solvable:Boolean = false
)