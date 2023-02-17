package org.combinators.solitaire.spider

import org.combinators.solitaire.domain._

/** Additional shared code for spider variations with face-down cards
  */
trait closedSpiderVariationPoints extends spiderVariationPoints {

  override def getDeal: Seq[DealStep] = {
    var colNum:Int = 0
    var dealSeq:Seq[DealStep] = Seq()// doesn't like me declaring it without initializing
    //first four piles get 5 face down cards
    for (colNum <- 0 to 3) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = 5))
    }
    //the rest get 4 face down cards
    for (colNum <- 4 to 9) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = 4))
    }
    //each pile gets a face up card
    colNum = 0
    for (colNum <- 0 to 9) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload())
    }
    dealSeq
  }

  val allowed = AndConstraint(NotConstraint(IsEmpty(Source)), NotConstraint(IsFaceUp(TopCardOf(Source))))
  val flipMove:Move = FlipCardMove("FlipCard", Press, source = (Tableau, allowed))
}