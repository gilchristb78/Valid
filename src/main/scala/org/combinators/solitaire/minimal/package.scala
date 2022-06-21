package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object minimal {

  def numTableau() :Int = 6

  val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](numTableau())(Column),
    Foundation -> Seq.fill[Element](4)(Pile),
    StockContainer -> Seq(Stock())
  )

  val layout:Layout = Layout(Map(
    StockContainer -> horizontalPlacement(15, 20, 1, card_height),
    Foundation -> horizontalPlacement(120, 20, 4, card_height),
    Tableau -> horizontalPlacement(15, 120, numTableau(), 13*card_height)
  ))

  val deckDealMove:Move = DealDeckMove("DealDeck", 1,
    source=(StockContainer, NotConstraint(IsEmpty(Source))), target=Some((Tableau, Truth)))

  def allowed(): Constraint = {
    val topDestination = TopCardOf(Destination)
    val isEmpty = IsEmpty(Destination)

    OrConstraint(isEmpty, NextRank(topDestination, MovingCard))
  }

  def allowedMT(): Constraint = {
    val topDestination = TopCardOf(Destination)
    val isEmpty = IsEmpty(Destination)

    OrConstraint(isEmpty, NextRank(topDestination, BottomCardOf(MovingCards)))
  }

  val tableauToTableauMove:Move = SingleCardMove("MoveCard", Drag,
    source=(Tableau,Truth), target=Some((Tableau, allowed())))

  // HAS to be SingleCardMove, because from the Tableau (t2tmove) you can initiate single card moves. Must be consistent
  val t2fMove:Move = SingleCardMove("MoveCardFoundation", Drag,
    source=(Tableau,Truth), target=Some((Foundation, IsSingle(MovingCard))))


  val tableauToTableauMoveMT:Move = MultipleCardsMove("MoveCards", Drag,
    source=(Tableau,Truth), target=Some((Tableau, allowedMT())))

  // HAS to be SingleCardMove, because from the Tableau (t2tmove) you can initiate single card moves. Must be consistent
  val t2fMoveMT:Move = MultipleCardsMove("MoveCardsFoundation", Drag,
    source=(Tableau,Truth), target=Some((Foundation, IsSingle(MovingCards))))

  case object TableauToEmptyTableau extends Setup {
    val sourceElement = ElementInContainer(Tableau, 1)
    val targetElement = Some(ElementInContainer(Tableau, 2))

    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      RemoveStep(targetElement.get),
      MovingCardStep(CardCreate(Clubs, Eight))
    )
  }

  val minimalS:Solitaire = {

    Solitaire( name="Minimal",
      structure = structureMap,
      layout = layout,
      deal = Seq(DealStep(ContainerTarget(Tableau))),

      /** from element can infer ks.ViewWidget as well as Base Element. */
      specializedElements = Seq.empty,

      /** All rules here. */
      moves = Seq(tableauToTableauMove, deckDealMove, t2fMove),

      // fix winning logic
      logic = BoardState(Map(Tableau -> 0)),

      customizedSetup = Seq(TableauToEmptyTableau)
    )
  }

  val minimalM:Solitaire = {

    Solitaire( name="Minimal",
      structure = structureMap,
      layout = layout,
      deal = Seq(DealStep(ContainerTarget(Tableau))),

      /** from element can infer ks.ViewWidget as well as Base Element. */
      specializedElements = Seq.empty,

      /** All rules here. */
      moves = Seq(tableauToTableauMoveMT, deckDealMove, t2fMoveMT),

      // fix winning logic
      logic = BoardState(Map(Tableau -> 0))
    )
  }
}
