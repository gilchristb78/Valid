package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.spider.variationPoints


package object spiderwort extends variationPoints {

  //TODO behaves strangely when overriding numTableau/Foundation, hardcoded in maps for now
  //override val numTableau:Int = 13
  //override val numFoundation:Int = 12
  //override val numStock:Int = 3


  //Gigantic is like a marathon spider, with twice as many decks
  override val map:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> horizontalPlacement(15, 200, 13, 13*card_height),
    StockContainer -> horizontalPlacement(15, 20, 1, card_height),
    Foundation -> horizontalPlacement(150, 20, 12, card_height)
  )


  override val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](13)(BuildablePile),
    Foundation -> Seq.fill[Element](12)(Pile),
    StockContainer -> Seq(Stock(3))
  )

  override def getDeal: Seq[DealStep] = {
    var colNum: Int = 0
    var dealSeq: Seq[DealStep] = Seq()
    for (colNum <- 0 to 5) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = 5))
    }
    colNum = 7
    for (colNum <- 6 to 12) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = 4))
    }

    //each pile gets a face up card
    colNum = 0
    for (colNum <- 0 to 12) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 1))
    }

    dealSeq
  }

  val spiderwort:Solitaire = {
    Solitaire(name = "Spiderwort",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove, flipMove),
      logic = BoardState(Map(Foundation -> 156)),
      solvable = false
    )
  }
}