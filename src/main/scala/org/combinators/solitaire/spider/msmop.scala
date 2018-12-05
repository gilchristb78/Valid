package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.spider.variationPoints


package object mrsmop extends variationPoints {

  //TODO behaves strangely when overriding numTableau/Foundation, hardcoded in maps for now
  //override val numTableau:Int = 13
  //override val numFoundation:Int = 4
  //override val numStock:Int = 2

  override val map:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> horizontalPlacement(15, 200, 13, 13*card_height),
    StockContainer -> horizontalPlacement(15, 20, 1, card_height),
    Foundation -> horizontalPlacement(293, 20, 8, card_height)
  )


  override val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](13)(BuildablePile),
    Foundation -> Seq.fill[Element](8)(Pile),
    StockContainer -> Seq(Stock(2))
  )

  //Ms. Mop uses a very straightforward deal, thirteen columns of 8 face-up cards
  override def getDeal: Seq[DealStep] = {
    var colNum: Int = 0
    var dealSeq: Seq[DealStep] = Seq()
    colNum = 0
    for (colNum <- 0 to 12) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 8))
    }

    dealSeq
  }

  val mrsmop:Solitaire = {
    Solitaire(name = "MrsMop",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove),
      logic = BoardState(Map(Foundation -> 104)),
      solvable = false
    )
  }
}