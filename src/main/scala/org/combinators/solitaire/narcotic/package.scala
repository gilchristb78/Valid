package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.templating.twirl.Java

package object narcotic {
  case class ToLeftOf(destination: MoveInformation, src:MoveInformation) extends Constraint
  case class AllSameRank(src:MoveInformation) extends Constraint

  val numTableau:Int = 4
  val map:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](numTableau)(Pile),
    StockContainer -> Seq(Stock())
  )

  val sameRank =  SameRank(MovingCard, TopCardOf(Destination))
  val toLeftOf =  ToLeftOf(Destination, Source) // -- causes problems. not sure why
  val tt_move = AndConstraint(sameRank, toLeftOf)  // , sameRank)   // NotConstraint(IsEmpty(Destination))

  val tableauToTableauMove:Move = SingleCardMove("MoveCard", Drag,
    source=(Tableau,Truth), target=Some((Tableau, tt_move)))

  val tableauRemove:Move = RemoveMultipleCardsMove("RemoveAllCards", Press,
    source=(Tableau, AllSameRank(Tableau)), target=None)

  val deck_move = NotConstraint(IsEmpty(Source))
  val deckDealMove:Move = DealDeckMove("DealDeck", 1,
    source=(StockContainer, deck_move), target=Some((Tableau, Truth)))

  // reset deck by pulling together all cards from the piles.
  val deckReset:Move = ResetDeckMove("ResetDeck",
    source=(StockContainer,IsEmpty(Source)), target=Some((Tableau, Truth)))

  val narcotic:Solitaire = {

    Solitaire( name="Narcotic",
      structure = map,
      layout=stockTableauPileLayout(4),
      deal = Seq(DealStep(ContainerTarget(Tableau))),
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove,tableauRemove,deckDealMove,deckReset),
      logic = BoardState(Map(Tableau -> 0, StockContainer -> 0)),
      customizedSetup = Seq.empty
    )
  }
}
