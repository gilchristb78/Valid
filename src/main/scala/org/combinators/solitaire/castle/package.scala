package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.templating.twirl.Java

package object castle {


  val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](8)(Column), // used to be column
    Foundation -> Seq.fill[Element](4)(Pile),
    StockContainer -> Seq(Stock(1))
  )
  val points: Seq[(Int, Int)] = Seq ((100,200), (203,200), (306, 200), (409, 200), (615, 200), (718, 200), (821,200), (924,200))
  val foundationPoints: Seq[(Int, Int)] = Seq ((512, 100), (512, 200) ,(512, 300), (512, 400))
  val layoutMap:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> calculatedPlacement(points, height = card_height*2),
    Foundation -> calculatedPlacement(foundationPoints, height = card_height)
  )
  def getDeal: Seq[Step] = {
    var deal:Seq[Step] = Seq (FilterStep(IsAce(DealComponents))) // remove all aces
    deal = deal :+ DealStep(ElementTarget(Foundation, 0))
    deal = deal :+ DealStep(ElementTarget(Foundation, 1))
    deal = deal :+ DealStep(ElementTarget(Foundation, 2))
    deal = deal :+ DealStep(ElementTarget(Foundation, 3))
    var colNum = 0
    while (colNum < 8)
    {
      deal = deal :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards =  6))
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

  def setBoardState: Seq[Java] = {
    Seq(Java(
      s"""
         |
         |Card movingCards = new Card(Card.THREE, Card.HEARTS);
         |game.tableau[1].removeAll();
         |game.tableau[2].removeAll();
         |game.foundation[2].add(new Card(Card.ACE, Card.HEARTS));
         |game.foundation[2].add(new Card(Card.TWO, Card.HEARTS));
      """.stripMargin))}

  /**
    * Clear 0th Foundation and place [Two, Ace] on the 0th Tableau
    */
  case object PrepareTableauToFoundation extends Setup {

    val sourceElement = ElementInContainer(Foundation, 0)
    val targetElement = Some(ElementInContainer(Tableau, 1))

    // clear Foundation, and place [2C, AC] on 0th tableau
    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),     // might not be necessary?
      InitializeStep(sourceElement, CardCreate(Spades, Ace)),
      InitializeStep(ElementInContainer(Foundation, 1), CardCreate(Spades, Ace)),
      InitializeStep(ElementInContainer(Foundation, 1), CardCreate(Clubs, Two)),
    )
  }

  val tt_move:Constraint = IfConstraint(IsEmpty(Destination), buildOnEmptyTableau(MovingCard), buildOnTableau(MovingCard))

  val tf_move:Constraint = IfConstraint(IsEmpty(Destination), buildOnEmptyFoundation(MovingCard), buildOnFoundation(MovingCard))

  val tableauToTableau:Move = SingleCardMove("MoveCard", Drag,
    source=(Tableau,Truth), target=Some((Tableau, tt_move)))

  val tableauToFoundation:Move = SingleCardMove("MoveCardFoundation", Drag,
    source=(Tableau,Truth), target=Some((Foundation, tf_move)))


  val castle:Solitaire = {

    Solitaire(name="Castle",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal,

      /** from element can infer ks.ViewWidget as well as Base Element. */
      specializedElements = Seq.empty,

      /** All rules here. */
      moves = Seq(tableauToTableau, tableauToFoundation),
      logic = BoardState(Map(Tableau -> 0, Foundation -> 52)),
      solvable = true,
      customizedSetup = Seq(PrepareTableauToFoundation)
    )
  }
}
