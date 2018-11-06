package org.combinators.solitaire

//import domain.constraints.NotConstraint
//import domain.constraints.movetypes.TopCardOf
//import domain.moves.DeckDealNCardsMove
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

  //scala loop
  var colNum:Int = 0
  var blah:Int = 0
  var dealSeq:Seq[DealStep]
  for (colNum <- 0 to 4) {
    dealSeq = dealSeq :+ DealStep(ElementTarget(SolitaireContainerTypes.Tableau, colNum), Payload(faceUp = false, 5))
    blah = blah + 1
  }
  for (colNum <- 4 to 10) {
    dealSeq = dealSeq :+ DealStep(ElementTarget(SolitaireContainerTypes.Tableau, colNum), Payload(faceUp = false, 4))
    blah = blah + 1
  }
  colNum = 0
  for (colNum <- 0 to 10) {
    dealSeq = dealSeq :+ DealStep(ElementTarget(SolitaireContainerTypes.Tableau, colNum), Payload(faceUp = true, 1))
    blah = blah + 1
  }

  val isEmpty = IsEmpty(Destination)
  val nextOne =  NextRank(TopCardOf(Destination), MovingCard) //maybe not needed?

  val topMoving = TopCardOf(MovingCards)
  val bottomMoving = BottomCardOf(MovingCards)
  val topDestination = TopCardOf(Destination)
  val topSource = TopCardOf(Source)

  //constraint to the destination
  val or = OrConstraint(isEmpty, NextRank(TopCardOf(Destination),
    BottomCardOf(MovingCards)))

  //constraint to the source
  val descend = Descending(MovingCards)
  val and = AndConstraint(descend, AllSameSuit(MovingCards))

  val tableauToTableau:Move = MultipleCardsMove ("MoveColumn", Drag,
    source=(Tableau, and),  target=Some((Tableau, or))) //TODO what exactly does this "some" mean?

  //3. tableau to foundation
  val pileFinish = AndConstraint(and, AndConstraint(IsAce(topMoving), IsKing(bottomMoving)))

  val buildFoundation:Move = MultipleCardsMove("BuildFoundation", Drag,
    source=(Tableau, pileFinish), target=Some((Foundation, isEmpty)))

  //4. flip card
  //TODO MAKE THIS WORK
  val faceDown = NotConstraint(IsFaceUp(topSource))
  val flip:Move = FlipCardMove("FlipCard", Press, source=(Tableau, faceDown)) //is this the way? Do I need to say Press like other moves say Drag?

  // Deal card from deck
  //how to do this?? Currently, this is just for moving the top card of the deck to a waste pile
  val deck_move = NotConstraint(IsEmpty(Source))
  //val deckDeal:Move = DealDeckMove("DealDeck", 1, source=(StockContainer, deck_move), target=Some((Waste, Truth)))
  //does this work? How to have Deal N cards do one to each tableau?
  //val deckDeal:Move = DeckDealNCardsMove(10, "DealDeck", source=(StockContainer, deck_move), target=Some((Tableau, Truth)))
  //this might be it, according to other usage. Code says multiple targets by default, so if going to the tableau and 10 do 1 to each?
  val deckDeal:Move = DeckDeal("DealDeck", 10, source=(StockContainer, deck_move), target=Some((Tableau, Truth)))

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

      //defined above
      deal = dealSeq,

      /** from element can infer ks.ViewWidget as well as Base Element. */
      //TODO ditch this? Does spider have any specialized elements?
      //specializedElements = Seq(WastePile),

      /** All rules here. */
      moves = Seq(tableauToTableau,buildFoundation,flip,deckDeal),

      // fix winning logic
      logic = BoardState(Map(Foundation -> 52))
    )
  }
}
