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
case object ShuffleDeck extends MoveType
case object RemoveStack extends MoveType


case class Move
(
  name:String,
  moveType:MoveType,
  gesture:GestureType,
  movableElement:Element,
  source:(ContainerType,Constraint),             // MUST be present
  target:Option[(ContainerType,Constraint)],     // has to be optional BECAUSE some moves do not involve any designated target
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

trait Setup {
  val setup:Seq[SetupStep]

  val sourceElement:ElementInContainer
  val targetElement:Some[ElementInContainer]

  // used to discriminate among move types, and can be extracted from sourceElement and targetElement
  def source: ContainerType = sourceElement.target
  def target: Option[ContainerType] = if (targetElement.isDefined) { Some(targetElement.get.target) } else { None}
}

trait SetupStep {
  val name:String
}
trait SetupTarget {
  val name:String    // every target must be able to report its name
}

// Note: The premise behind falsifiedTest() is flawed. Specifically, given a condition
// that is OR(c1, c2) and if you attempt to falsify with OR(not c1, c2) to demonstrate
// an error, it could still succeed, because of c2. So we are only going to work on
// positive test cases, to validate that a move works.

/** Designate a target  */
case class ElementInContainer(target:ContainerType, index:Int) extends SetupTarget {
  override val name = target.name + "[" + index + "]"
}

case class CardCreate(suit:Suit, rank:Rank)

// request, for example, to place an Ace of Spades on Tableau[0]
case class InitializeStep(target:SetupTarget, card:CardCreate) extends SetupStep {
  override val name = target.name
}
case class RemoveStep(target:SetupTarget) extends SetupStep {
  override val name = target.name
}
case class MovingCardStep(card:CardCreate) extends SetupStep {
  override val name = "movingCard" // will be the card that
}
case class MovingCardsStep(card:Seq[CardCreate]) extends SetupStep {
  override val name = "movingCards" // will be the card that
}
case class Solitaire (
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
  solvable:Boolean = false,
  //testSetup:Seq[Java] = Seq(),              // @Before by making this a Seq, it simplifies handling case when there is no test
  customizedSetup:Seq[Setup] = Seq(),     // @Custom Setup Routines, which contain unique setups to validate certain moves
)
/*
testSetup:Seq[MethodDeclaration] = Seq()*/
