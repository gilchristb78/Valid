package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object spider {

  case class AllSameSuit(src:MoveInformation) extends Constraint

  val numTableau:Int = 10
  val numFoundation:Int = 8
  val map:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](numTableau)(BuildablePile),//(Column), // replacing column with buildable pile, will it still work?
    Foundation -> Seq.fill[Element](numFoundation)(Pile),
    //Waste -> Seq.fill[Element](1)(WastePile),
    StockContainer -> Seq(Stock(2))
  )

  val bottomMoving = BottomCardOf(MovingCards)
  val topMoving = TopCardOf(MovingCards)
  val topDestination = TopCardOf(Destination)
  val isEmpty = IsEmpty(Destination)
  val descend = Descending(MovingCards)

  val or = OrConstraint(isEmpty, NextRank(topDestination,
    bottomMoving))

  val tableauToTableau:Move = MultipleCardsMove ("MoveColumn", Drag,
    source=(Tableau, Truth), target=Some((Tableau, AndConstraint(descend, or))))

  val f_and = AndConstraint(descend, AndConstraint(IsAce(topMoving), IsKing(bottomMoving)))

  val buildFoundation:Move = MultipleCardsMove("BuildFoundation", Drag,
    source=(Tableau, Truth), target=Some((Foundation, AndConstraint(f_and, isEmpty))))

  // Deal card from deck
  val deckDealMove:Move = DealDeckMove("DealDeck", 1,
    source=(StockContainer, Truth), target=Some((Tableau, Truth)))

  val spider:Solitaire = {

    Solitaire( name="Spider",
      structure = map,
      solvable = false,

      layout = Layout(Map(
        Tableau -> horizontalPlacement(15, 200, numTableau, 13*card_height),
        StockContainer -> horizontalPlacement(15, 20, 1, card_height),
        Foundation -> horizontalPlacement(293, 20, numFoundation, card_height),
        Waste -> horizontalPlacement(95, 20, 1, card_height)
      )),

      deal = Seq(DealStep(ContainerTarget(Tableau))),

      specializedElements = Seq.empty,

      /** All rules here. */
      moves = Seq(tableauToTableau
        ,deckDealMove
        ,buildFoundation),

      // fix winning logic
      logic = BoardState(Map(Foundation -> 52))
    )
  }
}
