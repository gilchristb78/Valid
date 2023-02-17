package org.combinators.solitaire.freecell

import org.combinators.solitaire.domain._

/**
 * Four initial cards dealt to the reserve. Only kings allowed in empty tableaus
 */
package object stalactites extends variationPoints  {

  override val structureMap:Map[ContainerType,Seq[Element]] = Map(
    StockContainer -> Seq.fill[Element](1)(Stock(numDecks)),   // a deck must be present even though not visible
    Tableau -> Seq.fill[Element](8)(Column),
    Foundation -> Seq.fill[Element](4)(Pile),
    Reserve -> Seq.fill[Element](2)(FreeCellPile),
  )

  override val layoutMap:Map[ContainerType, Seq[Widget]] = Map (
    Reserve -> horizontalPlacement(10, 10, 2, card_height),
    Foundation -> horizontalPlacement(400, 10, 4, card_height),
    Tableau -> horizontalPlacement(10, 200, num = 8, height = card_height*8),  // estimate
  )

  override def buildOnFoundation(card: MovingCard.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    OrConstraint(AndConstraint(SameSuit(card, topDestination), IsKing(topDestination), IsAce(card)),
      AndConstraint(NextRank(card, topDestination),  SameSuit(card, topDestination)))
  }
  override def buildOnFoundationMultiple(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    OrConstraint(AndConstraint(SameSuit(BottomCardOf(cards), topDestination), IsKing(topDestination), IsAce(BottomCardOf(cards))),
      AndConstraint(NextRank(BottomCardOf(cards), topDestination),  SameSuit(BottomCardOf(cards), topDestination)))
  }

  // never would have an empty foundation
  override def buildOnEmptyFoundation(card: MovingCard.type): Constraint = {
    Falsehood
  }

  override def buildOnEmptyFoundationMultiple(cards: MovingCards.type): Constraint = {
    Falsehood
  }

  def stalactitesDeal: Seq[DealStep] = {

      // deal four to reserve
      var deal:Seq[DealStep] = Seq(DealStep(ContainerTarget(Foundation)))
      var colNum = 0

      // Deal cards to all columns (from left to right) until none left, in which case
      // only first four have the extra cards.
      var numDealt = 4
      while (numDealt < 52) {
        deal = deal :+ DealStep(ElementTarget(Tableau, colNum), Payload())
        colNum += 1
        numDealt += 1
        if (colNum > 7) { colNum = 0}
      }

      deal
    }

  val stalactites:Solitaire = {

    Solitaire(name="Stalactites",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = stalactitesDeal,
      /** from element can infer ks.ViewWidget as well as Base Element. */
      specializedElements = Seq(FreeCellPile),

      /** All rules here. */
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, fromTableauToReserve, fromReserveToReserve, fromReserveToTableau, fromReserveToFoundation ),
      // fix winning logic
      logic = BoardState(Map(Foundation -> 52)),
      customizedSetup = Seq.empty,

      solvable = true,
      autoMoves = false   // because of wrap around, this fails...
    )
  }

}
