package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.spider.variationPoints


package object gigantic extends variationPoints {

  //TODO behaves strangely when overriding numTableau/Foundation, hardcoded in maps for now
  //override val numTableau:Int = 15
  //override val numFoundation:Int = 16
  //override val numStock:Int = 4

  override def numTableau(): Int ={
    15
  }

  override def numFoundation(): Int ={
    16
  }

  override def numStock(): Int ={
    4
  }
  /*
  //Gigantic is like a marathon spider, with twice as many decks
  override val map:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> horizontalPlacement(15, 200, 15, 13*card_height),
    StockContainer -> horizontalPlacement(15, 20, 1, card_height),
    Foundation -> horizontalPlacement(150, 20, 16, card_height)
  )


  override val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](15)(BuildablePile),
    Foundation -> Seq.fill[Element](16)(Pile),
    StockContainer -> Seq(Stock(4))
  )
*/
  override def getDeal: Seq[DealStep] = {
    var colNum: Int = 0
    var dealSeq: Seq[DealStep] = Seq()
    for (colNum <- 0 to 6) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = 5))
    }
    colNum = 7
    for (colNum <- 7 to 14) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = 4))
    }

    //each pile gets a face up card
    colNum = 0
    for (colNum <- 0 to 14) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 1))
    }

    dealSeq
  }

  val gigantic:Solitaire = {
    Solitaire(name = "Gigantic",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove, flipMove),
      logic = BoardState(Map(Foundation -> 208)),
      solvable = false
    )
  }
}