package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.fan.variationPoints
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

  def setBoardState: Seq[Java] = {
    Seq(Java(
      s"""
         |
         |Card movingCards = new Card(Card.THREE, Card.CLUBS);
         |game.tableau[1].removeAll();
         |game.tableau[2].removeAll();
         |game.tableau[2].add(new Card(Card.ACE, Card.CLUBS));
         |game.tableau[2].add(new Card(Card.TWO, Card.HEARTS));
         |game.foundation[2].add(new Card(Card.ACE, Card.SPADES));
         |game.foundation[2].add(new Card(Card.TWO, Card.DIAMONDS));
      """.stripMargin))}

  val scotchpatience:Solitaire = {
    Solitaire(name = "ScotchPatience",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove),
      logic = BoardState(Map(Tableau -> 0, Foundation -> 52)),
      solvable = true,
      testSetup = setBoardState,
    )
  }
}
