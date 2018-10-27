package org.combinators.solitaire

import domain.constraints.movetypes.TopCardOf
import org.combinators.solitaire.domain._

package object spider {

  case class AllSameSuit(src:MoveInformation) extends Constraint

  //case object WastePile extends Element (true)

  val numTableau:Int = 10
  val numFoundation:Int = 8
  val map:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](numTableau)(Column),
    Foundation -> Seq.fill[Element](numFoundation)(Pile),
    Waste -> Seq.fill[Element](1)(WastePile),
    StockContainer -> Seq(Stock())
  )

  val isEmpty = IsEmpty(Destination)
  val nextOne =  NextRank(TopCardOf(Destination), MovingCard) //maybe not needed?

  val topMoving = TopCardOf(MovingCards)
  val bottomMoving = BottomCardOf(MovingCards)
  val topDestination = TopCardOf(Destination)
  val topSource = TopCardOf(Source)

  //constraint to the destination
  //got rid of first 'and'
  val or = OrConstraint(isEmpty, NextRank(TopCardOf(Destination),
    BottomCardOf(MovingCards)))

  //constraint to the source
  //TODO This still looks good to me... descending and all same suit. Just had to change and to be next rank
  val descend = Descending(MovingCards)
  val and = AndConstraint(descend, AllSameSuit(MovingCards))

  val tableauToTableau:Move = MultipleCardsMove ("MoveColumn", Drag,
    source=(Tableau, and),  target=Some((Tableau, or)))

  /* TODO
  //2. waste to tableau
  val moveCard= OrConstraint(isEmpty, NextRank(TopCardOf(Destination), MovingCard))
  val wasteToTableau:Move = SingleCardMove("MoveCard", Drag,
    source=(Waste,Truth), target=Some(Tableau, moveCard))
  */

  //3. tableau to foundation
  //val isSingle = IsSingle(MovingCards)
  /*
  val tf_tgt = IfConstraint(isEmpty,
    AndConstraint (IsSingle(MovingCards), IsAce(BottomCardOf(MovingCards))),
    AndConstraint (IsSingle(MovingCards),
      NextRank(BottomCardOf(MovingCards), TopCardOf(Destination)),
      SameSuit(BottomCardOf(MovingCards), TopCardOf(Destination))))
  */
  val pileFinish = AndConstraint(and, AndConstraint(IsAce(topMoving), IsKing(bottomMoving)))

  val buildFoundation:Move = MultipleCardsMove("BuildFoundation", Drag,
    //source=(Tableau, IsSingle(MovingCards)), target=Some((Foundation, tf_tgt)))
    source=(Tableau, pileFinish), target=Some((Foundation, isEmpty)))

  /*
  val wf_tgt =  IfConstraint(isEmpty,
     AndConstraint ( IsSingle(MovingCard), IsAce(MovingCard)),
     AndConstraint( NextRank(MovingCard, TopCardOf(Destination)),
       SameSuit(MovingCard, TopCardOf(Destination))))

  val buildFoundationFromWaste:Move = SingleCardMove("BuildFoundationFromWaste", Drag,
    source=(Waste, Truth), target=Some((Foundation, wf_tgt)))
  */

  // Deal card from deck
  //how to do this?? Currently, this is just for moving the top card of the deck to a waste pile
  val deck_move = NotConstraint(IsEmpty(Source))
  val deckDeal:Move = DealDeckMove("DealDeck", 1, source=(StockContainer, deck_move), target=Some((Waste, Truth)))

  // reset deck if empty. Move is triggered by press on stock.
  // this creates DeckToPile, as in the above DeckDealMove.
  val deckReset:Move =  ResetDeckMove("ResetDeck", source=(StockContainer, IsEmpty(Source)), target=Some(Waste,Truth))

  //TODO ADD FLIP MOVE

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

      deal = Seq(DealStep(ContainerTarget(Tableau), Payload(faceUp=true, 4)),
        DealStep(ContainerTarget(Waste))
      ),

      /** from element can infer ks.ViewWidget as well as Base Element. */
      specializedElements = Seq(WastePile),

      /** All rules here. */
      moves = Seq(tableauToTableau,wasteToTableau,buildFoundation,buildFoundationFromWaste,deckDeal,deckReset),

      // fix winning logic
      logic = BoardState(Map(Foundation -> 52))
    )
  }
}
