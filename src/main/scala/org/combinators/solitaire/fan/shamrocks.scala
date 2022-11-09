package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.fan.variationPoints

import scala.collection.mutable.ListBuffer
import scala.util.Random

package object shamrocks extends variationPoints {
  case class MaxSizeConstraint(movingCards: MoveInformation, destination:MoveInformation, maxSize:Int) extends Constraint

  override def buildOnTableau (card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    AndConstraint(NextRank(topDestination, card),  SameSuit(card, topDestination))
    AndConstraint(OrConstraint( NextRank(topDestination, card),  NextRank(card, topDestination)),  MaxSizeConstraint(card, Destination, 3))
  }

  def dealKingsToBottom: Seq[Step] ={
    // remove all kings and makes sure that they are placed at the bottom of some random columns. Note
    // that this code computes random numbers, which THEN BECOME FIXED in the generated code. Perhaps
    // some solution could be found to have the generated code produce these random values!
    var deal:Seq[Step] = Seq (FilterStep(IsKing(DealComponents)))

    val rand = Random
    var visitedSpots:Seq[Int] = Seq()
    val seen = new ListBuffer[Int]()
    for (i <- 0 to 3 ) {
      var num = rand.nextInt(18)
      while (seen.contains(num)) {
        num = rand.nextInt(18)
      }
      seen += num

      visitedSpots = visitedSpots :+ num
      deal = deal :+ DealStep(ElementTarget(Tableau, num))
    }

    for (colNum <- 0 to 17) {
      var loadNum = 3
      if (visitedSpots.contains(colNum)){
        val oldSize = visitedSpots.size
        visitedSpots = visitedSpots.filter(e => e>colNum)
        loadNum -=(oldSize-visitedSpots.size)
      }
      if (colNum > 15) {
        loadNum -= 1
      }
      deal = deal :+ DealStep( ElementTarget(Tableau, colNum), Payload(numCards =  loadNum))
    }

    deal
  }

  val shamrocks:Solitaire = {
    Solitaire(name = "Shamrocks",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = dealKingsToBottom,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove),
      logic = BoardState(Map(Tableau -> 0, Foundation -> 52)),
      solvable = true,
      customizedSetup = Seq(TableauToEmptyFoundation, TableauToNextFoundation, TableauToEmptyTableau, TableauToNextTableau)
    )
  }
}
