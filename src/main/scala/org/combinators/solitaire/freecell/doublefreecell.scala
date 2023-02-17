package org.combinators.solitaire.freecell

import org.combinators.solitaire.domain._

/**
 * Any time you change a twirl template, you HAVE to force a regeneration. Unfortunately, this
 * is accomplished ONLY by launching a web server and connecting via localhost:9000
 */
package object doublefreecell extends variationPoints  {

  override val numDecks:Int = 2

  // can TURN corner from K back to ACE
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
  override val structureMap:Map[ContainerType,Seq[Element]] = Map(
    StockContainer -> Seq.fill[Element](2)(Stock(numDecks)),   // two decks must be present even though not visible
    Tableau -> Seq.fill[Element](10)(Column),        // TEN of these
    Foundation -> Seq.fill[Element](4)(Pile),
    Reserve -> Seq.fill[Element](6)(FreeCellPile),   // SIX of these
  )

  override val layoutMap:Map[ContainerType, Seq[Widget]] = Map (
    Reserve -> horizontalPlacement(10, 10, 6, card_height),
    Foundation -> horizontalPlacement(400+2*card_width, 10, 4, card_height),
    Tableau -> horizontalPlacement(10, 200, num = 10, height = card_height*8),  // estimate
  )

  def getDeal: Seq[DealStep] = {
    var deal:Seq[DealStep] = Seq()
    var colNum = 0
    // Deal cards to all columns (from left to right) until none left, in which case
    // only first four have the extra cards.
    var numDealt = 0
    while (numDealt < 52*numDecks) {
      deal = deal :+ DealStep(ElementTarget(Tableau, colNum), Payload())
      colNum += 1
      numDealt += 1
      if (colNum > 9) { colNum = 0}
    }

    deal
  }

  val doubleFreecell:Solitaire = {

    Solitaire(name="DoubleFreeCell",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal,
      /** from element can infer ks.ViewWidget as well as Base Element. */
      specializedElements = Seq(FreeCellPile),

      /** All rules here. */
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, fromTableauToReserve, fromReserveToReserve, fromReserveToTableau, fromReserveToFoundation ),
      // fix winning logic
      logic = BoardState(Map(Foundation -> 104)),
      customizedSetup = Seq.empty,

      solvable = true,
      autoMoves = false   // TOO Complicated, because of issue with multipleaces....
    )
  }

}
