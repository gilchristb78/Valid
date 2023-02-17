package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object napoleon {

  case class AllSameSuit(movingCards: MoveInformation) extends Constraint

  val numTableau:Int = 10
  val numFoundation:Int = 8
  val numDecks:Int = 2

  val napoleonMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](numTableau)(Column),
    Waste -> Seq.fill[Element](1)(WastePile),
    Foundation -> Seq.fill[Element](numFoundation)(Pile),
    StockContainer -> Seq(Stock(numDecks)),
  )

  val napoleonLayout:Map[ContainerType, Seq[Widget]] = Map (
    StockContainer -> horizontalPlacement(10, 10, 1, card_height),
    Waste -> horizontalPlacement(20 + card_width, 10, 1, card_height),
    Foundation -> horizontalPlacement(240, 10, 8, card_height),
    Tableau -> horizontalPlacement(10, 200, num = numTableau, height = card_height*8),  // estimate
  )

  /** 1. Deal cards from Deck if not empty. */
  def DeckDeal:Constraint = NotConstraint(IsEmpty(Source))
  val deckDeal:Move = DealDeckMove("DealDeck", 1,
    source=(StockContainer, DeckDeal), target=Some((Waste, Truth)))

  /** 2. Logic to determine how cards are moved from waste to foundation. */
  def WasteToFoundation:Constraint =  IfConstraint(IsEmpty(Destination),
    AndConstraint (IsSingle(MovingCard), IsAce(MovingCard)),
    AndConstraint(NextRank(MovingCard, TopCardOf(Destination)),
      SameSuit(MovingCard, TopCardOf(Destination))))
  val wasteToFoundation:Move = SingleCardMove("BuildFoundationFromWaste", Drag,
    source=(Waste, Truth), target=Some((Foundation, WasteToFoundation)))


  /** Logic to determine how Moving cards are to be built on tableau. */
  def ToTableau(): Constraint = {
    val topDestination = TopCardOf(Destination)
    val isEmpty = IsEmpty(Destination)
    OrConstraint(isEmpty,
        AndConstraint(NextRank(topDestination, MovingCard), SameSuit(topDestination, MovingCard)))
  }
  val tableauToTableau:Move = SingleCardMove("MoveTableau", Drag,
    source=(Tableau, Truth), target=Some((Tableau,ToTableau())))
  val wasteToTableau:Move = SingleCardMove("MoveWasteToTableau", Drag,
    source=(Waste, Truth), target=Some((Tableau,ToTableau())))

  /** Logic to determine how Moving cards are to be built on foundation from tableau. */
  def TableauToFoundation:Constraint =
      IfConstraint(IsEmpty(Destination),
        IsAce(MovingCard),
        AndConstraint (NextRank(MovingCard, TopCardOf(Destination)),
                       SameSuit(MovingCard, TopCardOf(Destination))))
  val tableauToFoundation:Move = SingleCardMove("BuildFoundation", Drag,
    source=(Tableau, Truth), target=Some((Foundation, TableauToFoundation)))

  val napoleon:Solitaire = {

    Solitaire( name="Napoleon",

      structure = napoleonMap,
      layout = Layout(napoleonLayout),
      deal = Seq(DealStep(ContainerTarget(Tableau), Payload(numCards = 4))),

      specializedElements = Seq(WastePile),

      /** All rules here. */
      moves = Seq(wasteToFoundation,deckDeal,tableauToFoundation,tableauToTableau,wasteToTableau),

      // fix winning logic
      logic = BoardState(Map(Foundation -> 52)),

      customizedSetup = Seq.empty
    )
  }
}
