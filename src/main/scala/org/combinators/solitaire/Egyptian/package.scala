package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object Egyptian {

  case class AllSameSuit(src:MoveInformation) extends Constraint

  case object WastePile extends Element (true)

  val numTableau:Int = 10
  val numFoundation:Int = 8
  val map:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](numTableau)(Column),
    Foundation -> Seq.fill[Element](numFoundation)(Pile),
    Waste -> Seq.fill[Element](1)(WastePile),
    StockContainer -> Seq(Stock(2))
  )

  val isEmpty = IsEmpty(Destination)
  val nextOne =  NextRank(TopCardOf(Destination), MovingCard)

  val bottomMoving = BottomCardOf(MovingCards)
  val topDestination = TopCardOf(Destination)

  //constraint to the destination
  val and= AndConstraint(NextRank(TopCardOf(Destination),
    BottomCardOf(MovingCards)), OppositeColor(TopCardOf(Destination),
    BottomCardOf(MovingCards)), AlternatingColors(MovingCards))
  val or = OrConstraint(isEmpty, and)

  //constraint to the source
  val descend = Descending(MovingCards)
  val and_2 = AndConstraint(descend, AllSameSuit(MovingCards))

  val tableauToTableau:Move = MultipleCardsMove ("MoveColumn", Drag,
    // NotConstraint(IsEmpty(Source))
    source=(Tableau, Truth),  target=Some((Tableau, or)))

  //2. waste to tableau
  val moveCard= OrConstraint(isEmpty, AndConstraint(NextRank(TopCardOf(Destination), MovingCard),OppositeColor(MovingCard, TopCardOf(Destination))))
  val wasteToTableau:Move = SingleCardMove("MoveCard", Drag,
    source=(Waste,Truth), target=Some(Tableau, moveCard))

  //3. waste to foundation  4.tableau to foundation
  val isSingle = IsSingle(MovingCards)
  val tf_tgt = IfConstraint(isEmpty,
    AndConstraint (IsSingle(MovingCards), IsAce(BottomCardOf(MovingCards))),
    AndConstraint (IsSingle(MovingCards),
      NextRank(BottomCardOf(MovingCards), TopCardOf(Destination)),
      SameSuit(BottomCardOf(MovingCards), TopCardOf(Destination))))

  val buildFoundation:Move = MultipleCardsMove("BuildFoundation", Drag,
    // NotConstraint(IsEmpty(Source))
    source=(Tableau, Truth), target=Some((Foundation, tf_tgt)))

  val wf_tgt =  IfConstraint(isEmpty,
     AndConstraint ( IsSingle(MovingCard), IsAce(MovingCard)),
     AndConstraint( NextRank(MovingCard, TopCardOf(Destination)),
       SameSuit(MovingCard, TopCardOf(Destination))))

  val buildFoundationFromWaste:Move = SingleCardMove("BuildFoundationFromWaste", Drag,
    source=(Waste, Truth), target=Some((Foundation, wf_tgt)))

  // Deal card from deck
  val deck_move = NotConstraint(IsEmpty(Source))
  val deckDeal:Move = DealDeckMove("DealDeck", 1, source=(StockContainer, deck_move), target=Some((Waste, Truth)))

  // reset deck if empty. Move is triggered by press on stock.
  // this creates DeckToPile, as in the above DeckDealMove.
  val deckReset:Move =  ResetDeckMove("ResetDeck", source=(StockContainer, IsEmpty(Source)), target=Some(Waste,Truth))

  def getDeal: Seq[DealStep] = {
    val colNum = 0;
    var dealSeq: Seq[DealStep] = Seq()
    var cardNum = 1;
    for (colNum <- 0 to 4) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards = cardNum))
      cardNum = cardNum + 2
    }
    cardNum = 10
    for (colNum <- 5 to 9) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards = cardNum))
      cardNum = cardNum - 2
    }

    dealSeq

  }


  val egyptian:Solitaire = {

    Solitaire( name="Egyptian",
      structure = map,

      layout = Layout(Map(
        Tableau -> horizontalPlacement(15, 200, numTableau, 13*card_height),
        StockContainer -> horizontalPlacement(15, 20, 1, card_height),
        Foundation -> horizontalPlacement(293, 20, numFoundation, card_height),
        Waste -> horizontalPlacement(95, 20, 1, card_height)
      )),

      deal = getDeal,

      /** from element can infer ks.ViewWidget as well as Base Element. */
      specializedElements = Seq(WastePile),

      /** All rules here. ,*/
      moves = Seq(tableauToTableau,wasteToTableau,buildFoundation,buildFoundationFromWaste,deckDeal,deckReset),

      // fix winning logic
      logic = BoardState(Map(Foundation -> 104))
    )
  }
}
