package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.golf.variationPoints

package object golf_no_wrap extends variationPoints{

  override def getNextRank() = OrConstraint(NextRank(MovingCard, TopCardOf(Destination), true), NextRank(TopCardOf(Destination), MovingCard, true))

  val golf_no_wrap:Solitaire = {

    Solitaire( name="Golf_no_wrap",
      structure = map,
      layout = golfLayout(),
      deal = getDeal(),
      specializedElements = Seq(WastePile),
      moves = Seq(tableauToWasteMove,deckDealMove),
      logic = BoardState(Map(Waste -> 52)),
      testSetup = Seq(),
    )
  }
}
