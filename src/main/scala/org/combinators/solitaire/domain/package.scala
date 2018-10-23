package org.combinators.solitaire

/**
  *
  */
package object domain {

  def SingleCardMove(name:String, gesture:GestureType,
                     source:(ContainerType,Constraint),
                     target:Option[(ContainerType,Constraint)]):Move = {
    Move(name, SingleCard, gesture, movableElement=Card,
      source=source, target=target)
  }

  def MultipleCardsMove(name:String, gesture:GestureType,
                     source:(ContainerType,Constraint),
                     target:Option[(ContainerType,Constraint)]):Move = {
    Move(name, MultipleCards, gesture, movableElement=Column,
      source=source, target=target)
  }

  // by default, deal to multiple destinations.
  def DealDeckMove(name:String,
                   numToDeal:Int,
                     source:(ContainerType,Constraint),
                     target:Option[(ContainerType,Constraint)]):Move = {
    Move(name, DealDeck(numToDeal), Press, movableElement=Card,
      source=source, target=target, isSingleDestination = false)
  }

  def ResetDeckMove(name:String,
                   source:(ContainerType,Constraint),
                   target:Option[(ContainerType,Constraint)]):Move = {
    Move(name, ResetDeck, Press, movableElement=Card,
      source=source, target=target, isSingleDestination = false)
  }

  def RemoveSingleCardMove(name:String, gesture:GestureType,
                     source:(ContainerType,Constraint),
                     target:Option[(ContainerType,Constraint)]):Move = {
    Move(name, RemoveSingleCard, gesture, movableElement=Card,
      source=source, target=target)
  }

  def RemoveMultipleCardsMove(name:String, gesture:GestureType,
                           source:(ContainerType,Constraint),
                           target:Option[(ContainerType,Constraint)]):Move = {
    Move(name, RemoveMultipleCards, gesture, movableElement=Card,
      source=source, target=target, isSingleDestination = false)
  }

  val card_width:Int = 73
  val card_height:Int = 97
  val card_overlap:Int = 22 // visible distance in columns.

  /** Common separation between widgets in layout. */
  val card_gap:Int = 15

  // case class Layout(places:Map[ContainerType, Seq[Widget]]) {

  def horizontalPlacement(topLeftX:Int, topLeftY:Int, num:Int, height:Int) : Seq[Widget] = {
    (0 until num).map(idx => Widget(topLeftX + idx*(card_gap+card_width), topLeftY, card_width, height))
  }

  def calculatedPlacement(pts:Seq[(Int,Int)], width:Int = card_width, height:Int = card_height) : Seq[Widget] = {
    pts.map(p => Widget(p._1, p._2, width, height))
  }

  // places:Map[ContainerType, Seq[Widget]]) {
  def stockTableauColumnLayout(numTableau:Int):Layout = {
    Layout(Map(
      StockContainer -> horizontalPlacement(15, 20, 1, card_height),
      Tableau -> horizontalPlacement(120, 20, numTableau, 13*card_height))
    )
  }

  def stockTableauPileLayout(numTableau:Int):Layout = {
    Layout(Map(
      StockContainer -> horizontalPlacement(15, 20, 1, card_height),
      Tableau -> horizontalPlacement(120, 20, numTableau, card_height))
    )
  }
}
