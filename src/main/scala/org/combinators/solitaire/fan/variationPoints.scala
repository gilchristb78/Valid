package org.combinators.solitaire.fan

import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.generic
import org.combinators.solitaire.domain._
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._
import org.combinators.templating.twirl.Java

/** Defines Fan's variation points
  */
trait variationPoints {
  val points: Seq[(Int, Int)] = Seq ((100,200), (203,200), (306, 200), (409, 200), (512, 200), (615, 200), (100,400), (203,400), (306, 400), (409, 400), (512, 400), (615, 400), (100,600), (203,600), (306, 600), (409, 600), (512, 600), (615, 600))
  val layoutMap:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> calculatedPlacement(points, height = card_height*2),
    Foundation -> horizontalPlacement(200, 10, 4, card_height),
  )

  val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](18)(Column),
    Foundation -> Seq.fill[Element](4)(Pile),
    StockContainer -> Seq(Stock(1))
  )

  def getDeal: Seq[DealStep] = {
    var deal:Seq[DealStep] = Seq()
    var colNum = 0
    //only first 16 cols get a third
    while (colNum < 18)
    {
      deal = deal :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards =  2))
      if (colNum < 16) {
        deal = deal :+ DealStep(ElementTarget(Tableau, colNum))
      }
      colNum += 1
    }
    deal
  }

  def buildOnTableau(card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    AndConstraint(NextRank(topDestination, card),  SameSuit(card, topDestination))
  }

  def buildOnEmptyTableau(card: MovingCard.type): Constraint = {
    IsKing(card)
  }

  def buildOnFoundation(card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    AndConstraint( NextRank(card, topDestination),  SameSuit(card, topDestination))
  }

  def buildOnEmptyFoundation(card: MovingCard.type): Constraint = {
    IsAce(card)
  }

  val tt_move:Constraint = IfConstraint(IsEmpty(Destination), buildOnEmptyTableau(MovingCard), buildOnTableau(MovingCard))

  val tf_move:Constraint = IfConstraint(IsEmpty(Destination), buildOnEmptyFoundation(MovingCard), buildOnFoundation(MovingCard))

  val tableauToTableauMove:Move = SingleCardMove("MoveCard", Drag,
    source=(Tableau,Truth), target=Some((Tableau, tt_move)))


  val tableauToFoundationMove:Move = SingleCardMove("MoveCardFoundation", Drag,
    source=(Tableau,Truth), target=Some((Foundation, tf_move)))


  case object TableauToEmptyFoundation extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Foundation, 2))

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      MovingCardStep(CardCreate(Clubs, Ace))
    )

    // Note: The premise behind falsifiedTest() is flawed. Specifically, given a condition
    // that is OR(c1, c2) and if you attempt to falsify with OR(not c1, c2) to demonstrate
    // an error, it could still succeed, because of c2. So we are only going to work on
    // positive test cases, to validate that a move works.

  }
  case object TableauToNextFoundation extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Foundation, 2))

    // clear Foundation, and place [2C, AC] on 1st tableau
    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      InitializeStep(targetElement.get, CardCreate(Clubs, Ace)),
      InitializeStep(targetElement.get, CardCreate(Clubs, Two)),
      MovingCardStep(CardCreate(Clubs, Three))
    )
  }

  case object TableauToEmptyTableau extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Tableau, 2))

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      MovingCardStep(CardCreate(Clubs, King))
    )
  }

  case object TableauToNextTableau extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Tableau, 2))

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      InitializeStep(targetElement.get, CardCreate(Clubs, Three)),
      InitializeStep(targetElement.get, CardCreate(Clubs, Two)),
      MovingCardStep(CardCreate(Clubs, Ace))
    )
  }
}