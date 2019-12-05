package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.fan.variationPoints
import org.combinators.templating.twirl.Java

import scala.util.Random




package object shamrocks extends variationPoints {
  case class MaxSizeConstraint(movingCards: MoveInformation, destination:MoveInformation, maxSize:Int) extends Constraint

  override def buildOnTableau (card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    AndConstraint( NextRank(topDestination, card),  SameSuit(card, topDestination))
    AndConstraint( OrConstraint( NextRank(topDestination, card),  NextRank(card, topDestination)),  MaxSizeConstraint(card, Destination, 3))
  }

  def getDeal2: Seq[Step] ={
    var deal:Seq[Step] = Seq (FilterStep(IsKing(DealComponents)))
    //only first 16 cols get a third
    val rand = Random
    var visitedSpots:Seq[Int] = Seq()
    var num = -1
    for (i <- 0 to 3 ){
      var num2 = rand.nextInt(18)
      while (num2 == num){
        num2 = rand.nextInt(18)
      }
      num = num2
      visitedSpots = visitedSpots :+ num
      deal = deal :+ DealStep(ElementTarget(Tableau, num))
    }
    for (colNum <- 0 to 17)
    {
      var loadNum = 3
      if (visitedSpots.contains(colNum)){
        val oldSize = visitedSpots.size
        visitedSpots = visitedSpots.filter(e => e>colNum)
        loadNum -=(oldSize-visitedSpots.size)
      }
      if (colNum > 15) {
        loadNum-=1
      }
      deal = deal :+ DealStep( ElementTarget(Tableau, colNum), Payload(numCards =  loadNum))
    }
    deal
  }

  def setBoardState: Seq[Java] = {
    Seq(Java(
      s"""
         |
         |Card movingCards = new Card(Card.THREE, Card.CLUBS);
         |game.tableau[1].removeAll();
         |game.tableau[2].removeAll();
         |game.tableau[2].add(new Card(Card.TWO, Card.HEARTS));
         |game.foundation[2].add(new Card(Card.ACE, Card.CLUBS));
         |game.foundation[2].add(new Card(Card.TWO, Card.CLUBS));
       """.stripMargin))}

  val shamrocks:Solitaire = {
    Solitaire(name = "Shamrocks",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal2,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove),
      logic = BoardState(Map(Tableau -> 0, Foundation -> 52)),
      solvable = true,
      testSetup = setBoardState,
    )
  }
}
