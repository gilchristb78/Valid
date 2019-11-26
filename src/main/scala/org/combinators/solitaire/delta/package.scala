package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object delta {

  //delta layout: 5 buildable piles in the tableau, 4 piles in the foundation
  val structureMap: Map[ContainerType, Seq[Element]] = Map(
    Tableau->Seq.fill[Element](8)(Column),
    Foundation->Seq.fill[Element](4)(Pile),
    StockContainer->Seq(Stock(1))
  )

  val layoutMap: Map[ContainerType, Seq[Widget]] = Map(
    Tableau->horizontalPlacement(200, 410, 8, card_height * 10),
    Foundation->horizontalPlacement(200, 10, 4, card_height),
    StockContainer->horizontalPlacement(100, 10, 1, card_height)
  )

  //delta deal: deal 7 face up cards to the first buildable pile in the tableau, then deal 1 face up card to each
  var deal:Seq[Step] = Seq()
  deal = deal :+ DealStep(ElementTarget(Tableau, 0), Payload(numCards = 7))
  deal = deal :+ DealStep(ContainerTarget(Tableau))

  //delta moves: we can build on either the tableau of foundation
  //tableau: we can move a single card from one buildable pile to another as long as it has a different suit and lower rank than the target
  //foundation: we can move cards from the tableau to the foundation, building down from king by suit

  var t_And = AndConstraint(NotConstraint(SameSuit(MovingCard, TopCardOf(Destination))),
      NextRank(TopCardOf(Destination), MovingCard, true))
  var t_Or = OrConstraint(IsEmpty(Destination), t_And)

  var f_And = AndConstraint(SameSuit(MovingCard, TopCardOf(Destination)), NextRank(TopCardOf(Destination), MovingCard, true))
  var f_If = IfConstraint(IsEmpty(Destination), IsKing(MovingCard), f_And)


  var tab_to_tab:Move = SingleCardMove("Tableau", Drag, source=(Tableau, Truth), target=Some(Tableau, t_Or))
  var tab_to_found:Move = SingleCardMove("Foundation", Drag, source=(Tableau, Truth), target=Some(Foundation, f_If))

  val deckDeal:Move = DealDeckMove("DealDeck", 1, source=(StockContainer, NotConstraint(IsEmpty(Source))),
    target = Some((Tableau, Truth)))

  val delta: Solitaire = {
    Solitaire(name = "delta",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = deal,
      specializedElements = Seq.empty,
      moves = Seq(tab_to_tab, tab_to_found, deckDeal),
      logic = BoardState(Map(Foundation->52)), //we win when we get all of the cards up to the Foundation
      solvable = false,
      testSetup = Seq()
    )
  }
}