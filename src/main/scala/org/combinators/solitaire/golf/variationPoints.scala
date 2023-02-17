package org.combinators.solitaire.golf

import org.combinators.solitaire.domain._

trait variationPoints {
 
  def getNumTableau: Int = 7
  def getNumDecks: Int = 1

  val map:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](getNumTableau)(Column),
    StockContainer -> Seq(Stock(getNumDecks)),
    Waste -> Seq.fill[Element](1)(WastePile)
  )

  def getNextRank: Constraint = OrConstraint(NextRank(MovingCard, TopCardOf(Destination)), NextRank(TopCardOf(Destination), MovingCard))

  val wasteMove = OrConstraint(IsEmpty(Destination), getNextRank)

  val tableauToWasteMove:Move = SingleCardMove("MoveCardToWaste", Drag,
    source=(Tableau,Truth), target=Some((Waste, wasteMove)))

  val deck_move = NotConstraint(IsEmpty(Source))

  val deckDealMove:Move = DealDeckMove("DealDeck", 1,
    source=(StockContainer, deck_move), target=Some((Waste, Truth)))

  def getDeal: Seq[DealStep] = {
    Seq(DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)),
      DealStep(ContainerTarget(Tableau)))
  }

  def golfLayout:Layout = {
    Layout(Map(
      StockContainer -> horizontalPlacement(15, 20, 1, card_height),
      Tableau -> horizontalPlacement(120, 20, getNumTableau, 5*card_height),
      Waste -> horizontalPlacement(15, 40 + card_height, 1, card_height)
    ))
  }
}
