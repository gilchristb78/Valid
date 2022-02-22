package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.fan.variationPoints
import org.combinators.templating.twirl.Java

package object fanfreepile extends variationPoints {

  case object FreePile extends Element(true)

  override val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](18)(Column),
    Foundation -> Seq.fill[Element](4)(Pile),
    Reserve -> Seq.fill[Element](2)(FreePile),
    StockContainer -> Seq(Stock(1))
  )
  override val layoutMap:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> calculatedPlacement(points, height = card_height*2),
    Foundation -> horizontalPlacement(200, 10, 4, card_height),
    Reserve -> calculatedPlacement(Seq((800,250),(800, 250+card_height+card_gap)))
  )
  val moveToFreePile:Move = SingleCardMove("MoveToFreePile", Drag,
    source=(Tableau,Truth), target=Some((Reserve, IsEmpty(Destination))))

  val moveFromFreePile:Move = SingleCardMove("MoveFromFreePile", Drag,
    source=(Reserve,Truth), target=Some((Tableau, tt_move)))

  def setBoardState: Seq[Java] = {
    Seq(Java(
      s"""
         |
         |Card movingCards = new Card(Card.THREE, Card.HEARTS);
         |game.tableau[1].removeAll();
         |game.tableau[2].removeAll();
         |game.foundation[2].add(new Card(Card.ACE, Card.HEARTS));
         |game.foundation[2].add(new Card(Card.TWO, Card.HEARTS));
      """.stripMargin))}

  /**
    * Moving from Reserve to anywhere but Tableau will through an error
    * Views from element as
    */
  val fanfreepile:Solitaire = {
    Solitaire(name = "FanFreePile",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal,
      specializedElements = Seq(FreePile),
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, moveFromFreePile, moveToFreePile),
      logic = BoardState(Map(Tableau -> 0, Foundation -> 52)),
      solvable = true,
      testSetup = setBoardState,
    )
  }
}
