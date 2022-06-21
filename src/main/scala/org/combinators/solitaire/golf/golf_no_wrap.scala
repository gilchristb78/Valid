package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.golf.variationPoints

package object golf_no_wrap extends variationPoints{

  override def getNextRank: Constraint = OrConstraint(NextRank(MovingCard, TopCardOf(Destination), wrapAround=true), NextRank(TopCardOf(Destination), MovingCard, wrapAround=true))

  val golf_no_wrap:Solitaire = {
    Solitaire( name="GolfNoWrap",
      structure = map,
      layout = golfLayout,
      deal = getDeal,
      specializedElements = Seq(WastePile),
      moves = Seq(tableauToWasteMove,deckDealMove),
      logic = BoardState(Map(Waste -> 52))
    )
  }
}
